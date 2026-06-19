package com.microservicio.cambios.service;

import com.microservicio.cambios.dto.solicitud_servicio.CrearSolicitudDTO;
import com.microservicio.cambios.dto.solicitud_servicio.SolicitudDTO;
import com.microservicio.cambios.dto.solicitud_servicio.SolicitudJiraDTO;
import com.microservicio.cambios.entity.solicitud_servicio.Solicitud;
import com.microservicio.cambios.enums.EstadoCambio;
import com.microservicio.cambios.enums.solicitud_servicio.Prioridad;
import com.microservicio.cambios.enums.solicitud_servicio.TipoSolicitud;
import com.microservicio.cambios.mapper.CambioMapper;
import com.microservicio.cambios.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final CambioMapper solicitudMapper;
    private final JiraService jiraService;

    @Transactional
    public SolicitudDTO crearSolicitud(CrearSolicitudDTO dto) {
        log.info("Creando solicitud - Tipo: {}, Título: {}", dto.getTipoSolicitud(), dto.getTitulo());

        if (dto.getTipoSolicitud() == null) {
            throw new RuntimeException("El tipo de solicitud es requerido");
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setTipoSolicitud(dto.getTipoSolicitud());
        solicitud.setTitulo(dto.getTitulo());
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setEstado(EstadoCambio.PENDIENTE);
        solicitud.setPrioridad(convertirPrioridad(dto.getPrioridad()));

        // ===== ASIGNAR NUEVOS CAMPOS =====
        solicitud.setUsuarioSolicitante(dto.getUsuarioSolicitante());
        solicitud.setAreaSolicitante(dto.getAreaSolicitante());
        solicitud.setResponsableAsignado(dto.getResponsableAsignado());

        if (dto.getFechaVencimiento() != null) {
            solicitud.setFechaVencimiento(dto.getFechaVencimiento().atStartOfDay());
        }

        calcularSLA(solicitud);

        Solicitud saved = solicitudRepository.save(solicitud);
        log.info("Solicitud guardada - Código: {}", saved.getCodigoTicket());

        try {
            SolicitudJiraDTO jiraDTO = new SolicitudJiraDTO();
            jiraDTO.setTipoSolicitud(dto.getTipoSolicitud());
            jiraDTO.setTitulo(dto.getTitulo());
            jiraDTO.setDescripcion(dto.getDescripcion());
            jiraDTO.setPrioridad(dto.getPrioridad());
            jiraDTO.setFechaVencimiento(dto.getFechaVencimiento());
            jiraDTO.setLabels(dto.getLabels());
            jiraDTO.setAssignee(dto.getResponsableAsignado() != null ? dto.getResponsableAsignado() : dto.getAssignee());
            jiraDTO.setSubtareas(dto.getSubtareas());

            String jiraKey = jiraService.crearTicketEnJira(jiraDTO);

            saved.setJiraTicketId(jiraKey);
            saved.setJiraUrl(jiraService.getTicketUrl(jiraKey));

            Solicitud updated = solicitudRepository.save(saved);
            return solicitudMapper.toDTO(updated);
        } catch (Exception e) {
            log.error("Error al crear ticket en Jira: {}", e.getMessage());
            return solicitudMapper.toDTO(saved);
        }
    }

    private Prioridad convertirPrioridad(String prioridadJira) {
        if (prioridadJira == null) return Prioridad.MEDIA;
        return switch (prioridadJira) {
            case "Highest", "High" -> Prioridad.ALTA;
            case "Medium" -> Prioridad.MEDIA;
            case "Low", "Lowest" -> Prioridad.BAJA;
            default -> Prioridad.MEDIA;
        };
    }

    private void calcularSLA(Solicitud solicitud) {
        LocalDateTime ahora = LocalDateTime.now();
        int horasSLA = switch (solicitud.getPrioridad()) {
            case ALTA -> 4;
            case MEDIA -> 24;
            case BAJA -> 72;
        };
        solicitud.setSlaFechaLimite(ahora.plusHours(horasSLA));
    }

    @Transactional
    public SolicitudDTO actualizarEstado(Long id, EstadoCambio nuevoEstado) {
        log.info("Actualizando estado de solicitud {} a {}", id, nuevoEstado);

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));

        EstadoCambio estadoAnterior = solicitud.getEstado();
        solicitud.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoCambio.EN_PROCESO && solicitud.getFechaAsignacion() == null) {
            solicitud.setFechaAsignacion(LocalDateTime.now());
        }

        if (nuevoEstado == EstadoCambio.COMPLETADA) {
            solicitud.setFechaResolucion(LocalDateTime.now());
        }

        Solicitud updated = solicitudRepository.save(solicitud);

        if (solicitud.getJiraTicketId() != null) {
            try {
                jiraService.actualizarEstadoJira(solicitud.getJiraTicketId(), nuevoEstado.name());
            } catch (Exception e) {
                log.warn("No se pudo actualizar el estado en Jira: {}", e.getMessage());
            }
        }

        return solicitudMapper.toDTO(updated);
    }

    public SolicitudDTO obtenerSolicitud(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));
        return solicitudMapper.toDTO(solicitud);
    }

    public SolicitudDTO obtenerPorCodigoTicket(String codigoTicket) {
        Solicitud solicitud = solicitudRepository.findByCodigoTicket(codigoTicket)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con código: " + codigoTicket));
        return solicitudMapper.toDTO(solicitud);
    }

    public List<SolicitudDTO> listarTodas() {
        return solicitudRepository.findAll().stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SolicitudDTO> listarPorEstado(EstadoCambio estado) {
        return solicitudRepository.findByEstado(estado).stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SolicitudDTO> listarPorTipo(TipoSolicitud tipo) {
        return solicitudRepository.findByTipoSolicitud(tipo).stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SolicitudDTO> listarPorTipoYEstado(TipoSolicitud tipo, EstadoCambio estado) {
        return solicitudRepository.findByTipoSolicitudAndEstado(tipo, estado).stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    public long contarTotal() {
        return solicitudRepository.count();
    }

    public long contarPorEstado(EstadoCambio estado) {
        return solicitudRepository.countByEstado(estado);
    }

    public long contarPorTipo(TipoSolicitud tipo) {
        return solicitudRepository.countByTipoSolicitud(tipo);
    }

    //new
    @Transactional
    public SolicitudDTO actualizarResponsable(Long id, String responsable) {
        log.info("Actualizando responsable de solicitud {}: {}", id, responsable);

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));

        solicitud.setResponsableAsignado(responsable);
        if (solicitud.getFechaAsignacion() == null) {
            solicitud.setFechaAsignacion(LocalDateTime.now());
        }

        Solicitud updated = solicitudRepository.save(solicitud);
        return solicitudMapper.toDTO(updated);
    }

    @Transactional
    public SolicitudDTO actualizarResolucion(Long id, String resolucion) {
        log.info("Actualizando resolución de solicitud {}: {}", id, resolucion);

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));

        solicitud.setResolucion(resolucion);
        if (solicitud.getEstado() == EstadoCambio.COMPLETADA && solicitud.getFechaResolucion() == null) {
            solicitud.setFechaResolucion(LocalDateTime.now());
        }

        Solicitud updated = solicitudRepository.save(solicitud);
        return solicitudMapper.toDTO(updated);
    }
}