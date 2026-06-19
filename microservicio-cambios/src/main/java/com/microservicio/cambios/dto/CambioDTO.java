package com.microservicio.cambios.dto;

import com.microservicio.cambios.enums.CategoriaCambio;
import com.microservicio.cambios.enums.EstadoCambio;
import com.microservicio.cambios.enums.RiesgoCambio;
import com.microservicio.cambios.enums.TipoCambio;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public record CambioDTO(
        Long id,

        String codigoTicket,

        String titulo,

        String descripcion,

        TipoCambio tipoCambio,

        CategoriaCambio categoriaCambio,

        EstadoCambio estado,

        RiesgoCambio riesgo,

        String sistemaAfectado,

        String usuarioSolicitante,

        String areaSolicitante,

        String responsableAsignado,

        String aprobadoPor,

        String jiraTicketId,

        String jiraUrl,

        LocalDateTime fechaCreacion,

        LocalDateTime fechaActualizacion,

        LocalDateTime fechaAprobacion,

        LocalDateTime fechaImplementacion,

        LocalDateTime fechaVencimiento
) {
}
