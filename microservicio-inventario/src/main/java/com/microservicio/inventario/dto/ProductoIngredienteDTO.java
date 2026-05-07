package com.microservicio.inventario.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductoIngredienteDTO {
    private Long productoId;
    private Long ingredienteId;
    private Integer cantidad;

    // Constructores
    public ProductoIngredienteDTO() {}

    public ProductoIngredienteDTO(Long productoId, Long ingredienteId, Integer cantidad) {
        this.productoId = productoId;
        this.ingredienteId = ingredienteId;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getIngredienteId() { return ingredienteId; }
    public void setIngredienteId(Long ingredienteId) { this.ingredienteId = ingredienteId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}