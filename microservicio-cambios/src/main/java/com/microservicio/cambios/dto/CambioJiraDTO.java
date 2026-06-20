package com.microservicio.cambios.dto;

import com.microservicio.cambios.enums.CategoriaCambio;
import com.microservicio.cambios.enums.RiesgoCambio;
import com.microservicio.cambios.enums.TipoCambio;

import java.time.LocalDate;
import java.util.List;

public record CambioJiraDTO(
        TipoCambio tipoCambio,
        CategoriaCambio categoriaCambio,
        String titulo,
        String descripcion,
        String sistemaAfectado,
        String planRollback,
        RiesgoCambio riesgo,
        LocalDate fechaImplementacion,
        LocalDate fechaVencimiento,
        List<String> labels,
        String assignee,
        List<SubtareaDTO> subtareas
) {
}
