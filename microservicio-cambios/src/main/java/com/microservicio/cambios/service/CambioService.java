package com.microservicio.cambios.service;

import com.microservicio.cambios.dto.CambioDTO;
import com.microservicio.cambios.dto.CambioJiraDTO;
import com.microservicio.cambios.dto.CrearCambioDTO;
import com.microservicio.cambios.entity.Cambio;
import com.microservicio.cambios.enums.EstadoCambio;
import com.microservicio.cambios.enums.RiesgoCambio;
import com.microservicio.cambios.enums.TipoCambio;
import com.microservicio.cambios.mapper.CambioMapper;
import com.microservicio.cambios.repository.CambioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CambioService {

    private final CambioRepository cambioRepository;
    private final CambioMapper solicitudMapper;
    private final JiraService jiraService;

    private static final Map<String, String> JIRA_USERS = Map.of(
            "Italo Rios",
            "712020:42effcb1-ca82-40cf-87d6-de82b280e37c"
    );

    @Transactional
    public CambioDTO crearSolicitud(CrearCambioDTO dto) {
        log.info("Creando solicitud - Tipo: {}, Título: {}", dto.tipoCambio(), dto.titulo());

        if (dto.tipoCambio() == null) {
            throw new RuntimeException("El tipo de cambio es requerido");
        }

        Cambio cambio = new Cambio();
        cambio.setTipoCambio(dto.tipoCambio());
        cambio.setCategoriaCambio(dto.categoriaCambio());
        cambio.setTitulo(dto.titulo());
        cambio.setDescripcion(dto.descripcion());
        cambio.setEstado(EstadoCambio.PENDIENTE);
        cambio.setRiesgo(dto.riesgo() != null ? dto.riesgo() : RiesgoCambio.MEDIO);
        cambio.setSistemaAfectado(dto.sistemaAfectado());
        cambio.setPlanRollback(dto.planRollback());
        cambio.setUsuarioSolicitante(dto.usuarioSolicitante());
        cambio.setAreaSolicitante(dto.areaSolicitante());
        cambio.setResponsableAsignado(dto.responsableAsignado());


        if (dto.fechaImplementacion() != null) {
            cambio.setFechaImplementacion(dto.fechaImplementacion().atStartOfDay());
        }
        if (dto.fechaVencimiento() != null) {
            cambio.setFechaVencimiento(dto.fechaVencimiento().atStartOfDay());
        }

        Cambio saved = cambioRepository.save(cambio);
        log.info("Solicitud guardada - Código: {}", saved.getCodigoTicket());

        try {
            CambioJiraDTO jiraDTO = new CambioJiraDTO(
                    dto.tipoCambio(),
                    dto.categoriaCambio(),
                    dto.titulo(),
                    dto.descripcion(),
                    dto.sistemaAfectado(),
                    dto.planRollback(),
                    dto.riesgo(),
                    dto.fechaImplementacion(),
                    dto.fechaVencimiento(),
                    dto.labels(),
                    dto.responsableAsignado(),
                    dto.subtareas()
            );

            String jiraKey = jiraService.crearTicketEnJira(jiraDTO);

            saved.setJiraTicketId(jiraKey);
            saved.setJiraUrl(jiraService.getTicketUrl(jiraKey));

            Cambio updated = cambioRepository.save(saved);
            return solicitudMapper.toDTO(updated);
        } catch (Exception e) {
            log.error("Error al crear ticket en Jira: {}", e.getMessage());
            return solicitudMapper.toDTO(saved);
        }
    }

    @Transactional
    public CambioDTO actualizarEstado(Long id, EstadoCambio nuevoEstado) {
        log.info("Actualizando estado de solicitud {} a {}", id, nuevoEstado);

        Cambio cambio = cambioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));

        switch (nuevoEstado) {
            case APROBADO -> {
                cambio.setFechaAprobacion(LocalDateTime.now());
            }
            case EN_IMPLEMENTACION -> {
                if (cambio.getFechaImplementacion() == null) {
                    cambio.setFechaImplementacion(LocalDateTime.now());
                }
            }
            case CERRADO -> {
                cambio.setFechaCierre(LocalDateTime.now());
            }
        }
        cambio.setEstado(nuevoEstado);

        Cambio updated = cambioRepository.save(cambio);

        if (cambio.getJiraTicketId() != null) {
            try {
                jiraService.actualizarEstadoJira(cambio.getJiraTicketId(), nuevoEstado.name());
            } catch (Exception e) {
                log.warn("No se pudo actualizar el estado en Jira: {}", e.getMessage());
            }
        }

        return solicitudMapper.toDTO(updated);
    }

    @Transactional
    public CambioDTO actualizarResponsable(Long id, String responsable) {
        Cambio cambio = cambioRepository.findById(id).orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        cambio.setResponsableAsignado(responsable);
        Cambio updated = cambioRepository.save(cambio);
        if (cambio.getJiraTicketId() != null) {
            String accountId = JIRA_USERS.get(responsable);
            jiraService.actualizarAsignado(cambio.getJiraTicketId(), accountId);
        }
        return solicitudMapper.toDTO(updated);
    }

    public CambioDTO obtenerSolicitud(Long id) {
        Cambio cambio = cambioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));
        return solicitudMapper.toDTO(cambio);
    }

    public CambioDTO obtenerPorCodigoTicket(String codigoTicket) {
        Cambio solicitud = cambioRepository.findByCodigoTicket(codigoTicket)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con código: " + codigoTicket));
        return solicitudMapper.toDTO(solicitud);
    }

    public List<CambioDTO> listarTodas() {
        return cambioRepository.findAll().stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<CambioDTO> listarPorEstado(EstadoCambio estado) {
        return cambioRepository.findByEstado(estado).stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<CambioDTO> listarPorTipo(TipoCambio tipo) {
        return cambioRepository.findByTipoCambio(tipo).stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<CambioDTO> listarPorTipoYEstado(TipoCambio tipo, EstadoCambio estado) {
        return cambioRepository.findByTipoCambioAndEstado(tipo, estado).stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    public long contarTotal() {
        return cambioRepository.count();
    }

    public long contarPorEstado(EstadoCambio estado) {
        return cambioRepository.countByEstado(estado);
    }

    public long contarPorTipo(TipoCambio tipo) {
        return cambioRepository.countByTipoCambio(tipo);
    }

}