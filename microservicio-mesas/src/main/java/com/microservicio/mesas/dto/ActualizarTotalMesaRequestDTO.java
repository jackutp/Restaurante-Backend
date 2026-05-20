// dto/ActualizarTotalMesaRequestDTO.java
package com.microservicio.mesas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarTotalMesaRequestDTO {

    @NotNull(message = "El total es obligatorio")
    private Double total;

    // Getters y Setters
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}