package com.microservicio.Producto.dto;

import com.microservicio.Producto.Entities.Categoria;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El precio es demasiado alto")
    private BigDecimal precio;
    @NotNull(message = "La categoría es obligatoria")
    private Categoria categoria;
}