package com.microservicio.Insumos.dto;
import com.microservicio.Insumos.Entities.UnidadMedida;
import com.microservicio.Insumos.Entities.EstadoInsumo;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class InsumoRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;
    @NotNull(message = "La unidad de medida es obligatoria")
    private UnidadMedida unidadMedida;
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock = 0;
    private EstadoInsumo estadoInsumo;
}