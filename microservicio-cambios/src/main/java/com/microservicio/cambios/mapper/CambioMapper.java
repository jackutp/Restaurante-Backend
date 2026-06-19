package com.microservicio.cambios.mapper;

import com.microservicio.cambios.dto.CambioDTO;
import com.microservicio.cambios.entity.Cambio;
import org.springframework.stereotype.Component;

@Component
public class CambioMapper {

    public CambioDTO toDTO(Cambio cambio) {
        if (cambio == null) return null;

        return new CambioDTO(
                cambio.getId(),
                cambio.getCodigoTicket(),
                cambio.getTitulo(),
                cambio.getDescripcion(),
                cambio.getTipoCambio(),
                cambio.getCategoriaCambio(),
                cambio.getEstado(),
                cambio.getRiesgo(),
                cambio.getSistemaAfectado(),
                cambio.getUsuarioSolicitante(),
                cambio.getAreaSolicitante(),
                cambio.getResponsableAsignado(),
                cambio.getAprobadoPor(),
                cambio.getJiraTicketId(),
                cambio.getJiraUrl(),
                cambio.getFechaCreacion(),
                cambio.getFechaActualizacion(),
                cambio.getFechaAprobacion(),
                cambio.getFechaImplementacion(),
                cambio.getFechaVencimiento()
        );
    }

    public Cambio toEntity(CambioDTO dto) {
        if (dto == null) return null;

        Cambio cambio = new Cambio();

        cambio.setId(dto.id());
        cambio.setCodigoTicket(dto.codigoTicket());
        cambio.setTitulo(dto.titulo());
        cambio.setDescripcion(dto.descripcion());
        cambio.setTipoCambio(dto.tipoCambio());
        cambio.setCategoriaCambio(dto.categoriaCambio());
        cambio.setEstado(dto.estado());
        cambio.setRiesgo(dto.riesgo());
        cambio.setSistemaAfectado(dto.sistemaAfectado());
        cambio.setUsuarioSolicitante(dto.usuarioSolicitante());
        cambio.setAreaSolicitante(dto.areaSolicitante());
        cambio.setResponsableAsignado(dto.responsableAsignado());
        cambio.setAprobadoPor(dto.aprobadoPor());
        cambio.setJiraTicketId(dto.jiraTicketId());
        cambio.setJiraUrl(dto.jiraUrl());
        cambio.setFechaCreacion(dto.fechaCreacion());
        cambio.setFechaActualizacion(dto.fechaActualizacion());
        cambio.setFechaAprobacion(dto.fechaAprobacion());
        cambio.setFechaImplementacion(dto.fechaImplementacion());
        cambio.setFechaVencimiento(dto.fechaVencimiento());

        return cambio;
    }
}