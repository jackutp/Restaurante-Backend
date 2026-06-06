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
        dto.setCodigoTicket(solicitud.getCodigoTicket());
        dto.setTitulo(solicitud.getTitulo());
        dto.setDescripcion(solicitud.getDescripcion());
        dto.setTipoSolicitud(solicitud.getTipoSolicitud());
        dto.setEstado(solicitud.getEstado());
        dto.setPrioridad(solicitud.getPrioridad());
        dto.setFechaVencimiento(solicitud.getFechaVencimiento());
        dto.setSlaFechaLimite(solicitud.getSlaFechaLimite());
        dto.setUsuarioSolicitante(solicitud.getUsuarioSolicitante());
        dto.setAreaSolicitante(solicitud.getAreaSolicitante());
        dto.setResponsableAsignado(solicitud.getResponsableAsignado());
        dto.setFechaAsignacion(solicitud.getFechaAsignacion());
        dto.setFechaResolucion(solicitud.getFechaResolucion());
        dto.setResolucion(solicitud.getResolucion());
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
        solicitud.setCodigoTicket(dto.getCodigoTicket());
        solicitud.setTitulo(dto.getTitulo());
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setTipoSolicitud(dto.getTipoSolicitud());
        solicitud.setEstado(dto.getEstado());
        solicitud.setPrioridad(dto.getPrioridad());
        solicitud.setFechaVencimiento(dto.getFechaVencimiento());
        solicitud.setSlaFechaLimite(dto.getSlaFechaLimite());
        solicitud.setUsuarioSolicitante(dto.getUsuarioSolicitante());
        solicitud.setAreaSolicitante(dto.getAreaSolicitante());
        solicitud.setResponsableAsignado(dto.getResponsableAsignado());
        solicitud.setFechaAsignacion(dto.getFechaAsignacion());
        solicitud.setFechaResolucion(dto.getFechaResolucion());
        solicitud.setResolucion(dto.getResolucion());
        solicitud.setJiraTicketId(dto.getJiraTicketId());
        solicitud.setJiraUrl(dto.getJiraUrl());

        return solicitud;
    }
}