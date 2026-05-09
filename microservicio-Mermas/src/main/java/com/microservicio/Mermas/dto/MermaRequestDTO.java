package com.microservicio.Mermas.dto;

import com.microservicio.Mermas.Entities.TipoMerma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MermaRequestDTO {

    @NotNull(message = "El tipo es obligatorio")
    private TipoMerma tipoMerma;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombreMerma;

    @NotBlank(message = "La cantidad es obligatoria")
    private String cantidad;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    private Integer referenciaId;

    private String unidadMedida;
}