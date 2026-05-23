// dto/ActualizarEstadoMesaRequestDTO.java
package com.microservicio.mesas.dto;

import com.microservicio.mesas.entity.EstadoMesa;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoMesaRequestDTO {
    @NotNull(message = "El estado es obligatorio")
    private EstadoMesa estado;

    private Double totalActual;

    private String ordenActualId;
}