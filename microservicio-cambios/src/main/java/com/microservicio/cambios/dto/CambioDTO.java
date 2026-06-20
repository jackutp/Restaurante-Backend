package com.microservicio.cambios.dto;

import com.microservicio.cambios.enums.CategoriaCambio;
import com.microservicio.cambios.enums.EstadoCambio;
import com.microservicio.cambios.enums.RiesgoCambio;
import com.microservicio.cambios.enums.TipoCambio;

import java.time.LocalDateTime;

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
        String planRollback,
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
        LocalDateTime fechaCierre,
        LocalDateTime fechaVencimiento
) {
}
