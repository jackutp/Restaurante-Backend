package com.microservicio.solicitudes.service;

import com.microservicio.solicitudes.dto.CrearSolicitudDTO;
import com.microservicio.solicitudes.dto.SolicitudDTO;
import com.microservicio.solicitudes.dto.SolicitudJiraDTO;
import com.microservicio.solicitudes.entity.Solicitud;
import com.microservicio.solicitudes.enums.EstadoSolicitud;
import com.microservicio.solicitudes.enums.Prioridad;
import com.microservicio.solicitudes.enums.TipoSolicitud;
import com.microservicio.solicitudes.mapper.SolicitudMapper;
import com.microservicio.solicitudes.repository.SolicitudRepository;
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
    private final SolicitudMapper solicitudMapper;
    private final JiraService jiraService;

    @Transactional
    public SolicitudDTO crearSolicitud(CrearSolicitudDTO dto) {
        log.info("Creando solicitud - Tipo: {}, Título: {}", dto.getTipoSolicitud(), dto.getTitulo());

        // Validar tipo
        if (dto.getTipoSolicitud() == null) {
            throw new RuntimeException("El tipo de solicitud es requerido (SERVICIO, INFORMACION o ACCESO)");
        }

        // Crear entidad
        Solicitud solicitud = new Solicitud();
        solicitud.setTipoSolicitud(dto.getTipoSolicitud());
        solicitud.setTitulo(dto.getTitulo());
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setPrioridad(convertirPrioridad(dto.getPrioridad()));

        if (dto.getFechaVencimiento() != null) {
            solicitud.setFechaVencimiento(dto.getFechaVencimiento().atStartOfDay());
        }

        // Calcular SLA
        calcularSLA(solicitud);

        // Guardar localmente
        Solicitud saved = solicitudRepository.save(solicitud);
        log.info("Solicitud guardada localmente - Código: {}", saved.getCodigoTicket());

        try {
            // Preparar DTO para Jira
            SolicitudJiraDTO jiraDTO = new SolicitudJiraDTO();
            jiraDTO.setTipoSolicitud(dto.getTipoSolicitud());
            jiraDTO.setTitulo(dto.getTitulo());
            jiraDTO.setDescripcion(dto.getDescripcion());
            jiraDTO.setPrioridad(dto.getPrioridad());
            jiraDTO.setFechaVencimiento(dto.getFechaVencimiento());
            jiraDTO.setLabels(dto.getLabels());
            jiraDTO.setAssignee(dto.getAssignee());
            jiraDTO.setSubtareas(dto.getSubtareas());

            // Crear ticket en Jira
            String jiraKey = jiraService.crearTicketEnJira(jiraDTO);

            saved.setJiraTicketId(jiraKey);
            saved.setJiraUrl(jiraService.getTicketUrl(jiraKey));

            Solicitud updated = solicitudRepository.save(saved);
            log.info("Solicitud actualizada con ticket Jira: {}", jiraKey);

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
    public SolicitudDTO actualizarEstado(Long id, EstadoSolicitud nuevoEstado) {
        log.info("Actualizando estado de solicitud {} a {}", id, nuevoEstado);

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        solicitud.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoSolicitud.EN_PROCESO && solicitud.getFechaAsignacion() == null) {
            solicitud.setFechaAsignacion(LocalDateTime.now());
        }

        if (nuevoEstado == EstadoSolicitud.COMPLETADA) {
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

    public List<SolicitudDTO> listarPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstado(estado).stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SolicitudDTO> listarPorTipo(TipoSolicitud tipo) {
        return solicitudRepository.findByTipoSolicitud(tipo).stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SolicitudDTO> listarPorTipoYEstado(TipoSolicitud tipo, EstadoSolicitud estado) {
        return solicitudRepository.findByTipoSolicitudAndEstado(tipo, estado).stream()
                .map(solicitudMapper::toDTO)
                .collect(Collectors.toList());
    }

    public long contarTotal() {
        return solicitudRepository.count();
    }

    public long contarPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.countByEstado(estado);
    }

    public long contarPorTipo(TipoSolicitud tipo) {
        return solicitudRepository.countByTipoSolicitud(tipo);
    }
}