package com.microservicio.solicitudes.service;

import com.microservicio.solicitudes.dto.SolicitudDTO;
import com.microservicio.solicitudes.entity.Solicitud;
import com.microservicio.solicitudes.enums.EstadoSolicitud;
import com.microservicio.solicitudes.mapper.SolicitudMapper;
import com.microservicio.solicitudes.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public SolicitudDTO crearSolicitud(SolicitudDTO solicitudDTO) {
        log.info("Creando nueva solicitud: {}", solicitudDTO.getTitulo());
        log.info("Descripción: {}", solicitudDTO.getDescripcion());  // ← LOG para depurar

        // Convertir DTO a Entity usando el mapper
        Solicitud solicitud = solicitudMapper.toEntity(solicitudDTO);

        // Si el mapper no funcionó, crear manualmente
        if (solicitud.getDescripcion() == null) {
            log.warn("La descripción es null, asignando manualmente");
            solicitud.setDescripcion(solicitudDTO.getDescripcion());
        }

        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        // Guardar localmente
        Solicitud saved = solicitudRepository.save(solicitud);
        log.info("Solicitud guardada con ID: {}, Título: {}, Descripción: {}",
                saved.getId(), saved.getTitulo(), saved.getDescripcion());

        try {
            // Crear ticket en Jira
            String jiraKey = jiraService.crearTicketEnJira(
                    saved.getTitulo(),
                    saved.getDescripcion()
            );

            // Actualizar con el ID de Jira
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

    @Transactional
    public SolicitudDTO actualizarEstado(Long id, EstadoSolicitud nuevoEstado) {
        log.info("Actualizando estado de solicitud {} a {}", id, nuevoEstado);

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));

        solicitud.setEstado(nuevoEstado);
        Solicitud updated = solicitudRepository.save(solicitud);

        // Si tiene ticket en Jira, actualizar también allá
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
}