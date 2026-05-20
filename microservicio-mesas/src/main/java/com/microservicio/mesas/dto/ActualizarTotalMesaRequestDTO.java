// dto/ActualizarTotalMesaRequestDTO.java
package com.microservicio.mesas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarTotalMesaRequestDTO {
    @NotNull(message = "El total es obligatorio")
    private Double total;
}