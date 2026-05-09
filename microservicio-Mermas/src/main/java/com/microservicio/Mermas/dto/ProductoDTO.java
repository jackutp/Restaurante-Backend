package com.microservicio.Mermas.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoDTO {
    private Integer productoid;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String categoria;
    private Integer stock;
}