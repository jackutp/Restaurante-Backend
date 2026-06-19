package com.microservicio.cambios.dto;

import com.microservicio.cambios.dto.solicitud_servicio.SubtareaDTO;
import com.microservicio.cambios.enums.CategoriaCambio;
import com.microservicio.cambios.enums.RiesgoCambio;
import com.microservicio.cambios.enums.TipoCambio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CrearCambioDTO(
        @NotNull(message = "El tipo de cambio es requerido")
        TipoCambio tipoCambio,

        @NotNull(message = "La categoría es requerida")
        CategoriaCambio categoriaCambio,

        @NotBlank(message = "El título es requerido")
        String titulo,

        @NotBlank(message = "La descripción es requerida")
        String descripcion,

        @NotBlank(message = "El sistema afectado es requerido")
        String sistemaAfectado,

        RiesgoCambio riesgo,

        String usuarioSolicitante,

        String areaSolicitante,

        String responsableAsignado,

        LocalDate fechaImplementacion
) {
}
