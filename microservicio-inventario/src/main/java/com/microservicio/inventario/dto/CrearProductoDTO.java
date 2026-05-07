package com.microservicio.inventario.dto;

import com.microservicio.inventario.Entities.CategoriaProducto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class CrearProductoDTO {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private CategoriaProducto categoria;

    // Constructores + Getters/Setters
    public CrearProductoDTO() {}

    public CrearProductoDTO(String nombre, String descripcion, BigDecimal precio, CategoriaProducto categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public CategoriaProducto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProducto categoria) { this.categoria = categoria; }
}