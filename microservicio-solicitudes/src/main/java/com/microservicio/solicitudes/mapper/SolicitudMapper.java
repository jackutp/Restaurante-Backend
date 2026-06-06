package com.microservicio.solicitudes.mapper;

import com.microservicio.solicitudes.dto.SolicitudDTO;
import com.microservicio.solicitudes.entity.Solicitud;
import org.springframework.stereotype.Component;

@Component
public class SolicitudMapper {

    public SolicitudDTO toDTO(Solicitud solicitud) {
        if (solicitud == null) return null;

        SolicitudDTO dto = new SolicitudDTO();
        dto.setId(solicitud.getId());
        dto.setTitulo(solicitud.getTitulo());
        dto.setDescripcion(solicitud.getDescripcion());  // ← Verifica este campo
        dto.setEstado(solicitud.getEstado());
        dto.setJiraTicketId(solicitud.getJiraTicketId());
        dto.setJiraUrl(solicitud.getJiraUrl());
        dto.setFechaCreacion(solicitud.getFechaCreacion());
        dto.setFechaActualizacion(solicitud.getFechaActualizacion());

        return dto;
    }

    public Solicitud toEntity(SolicitudDTO dto) {
        if (dto == null) return null;

        Solicitud solicitud = new Solicitud();
        solicitud.setId(dto.getId());
        solicitud.setTitulo(dto.getTitulo());
        solicitud.setDescripcion(dto.getDescripcion());  // ← Verifica este campo
        solicitud.setEstado(dto.getEstado());
        solicitud.setJiraTicketId(dto.getJiraTicketId());
        solicitud.setJiraUrl(dto.getJiraUrl());
        // No mapear fechas porque se setean automáticamente con @PrePersist

        return solicitud;
    }
}