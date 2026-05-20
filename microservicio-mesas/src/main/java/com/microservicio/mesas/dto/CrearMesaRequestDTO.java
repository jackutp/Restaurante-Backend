// dto/CrearMesaRequestDTO.java
package com.microservicio.mesas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearMesaRequestDTO {
    @NotNull(message = "El número de mesa es obligatorio")
    @Min(value = 1, message = "El número debe ser mayor a 0")
    private Integer numero;

    @Min(value = 1, message = "La capacidad mínima es 1")
    private Integer capacidad = 4;  // valor por defecto
}