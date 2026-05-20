// dto/MesaResponseDTO.java
package com.microservicio.mesas.dto;

import com.microservicio.mesas.entity.EstadoMesa;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MesaResponseDTO {
    private Long id;
    private Integer numero;
    private Integer capacidad;
    private EstadoMesa estado;
    private Double totalActual;
    private String ordenActualId;
}