package com.microservicio.cambios.dto;

import com.microservicio.cambios.enums.CategoriaCambio;
import com.microservicio.cambios.enums.TipoCambio;

import java.time.LocalDate;
import java.util.List;

public record SolicitudjiraDTO(
        TipoCambio tipoCambio,

        CategoriaCambio categoriaCambio,

        String titulo,

        String descripcion,

        String sistemaAfectado,

        String riesgo,

        LocalDate fechaImplementacion,

        List<String> labels,

        String assignee,

        List<SubtareaDTO> subtareas
) {
}
