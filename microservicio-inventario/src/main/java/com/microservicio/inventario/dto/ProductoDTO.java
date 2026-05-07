package com.microservicio.inventario.dto;

import com.microservicio.inventario.Entities.CategoriaProducto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
public class ProductoDTO {
    private Long productoId;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private CategoriaProducto categoria;
    private List<IngredienteDTO> ingredientes; // Solo IDs o nombres para evitar circularidad

    // Constructores
    public ProductoDTO() {}

    public ProductoDTO(Long productoId, String nombre, String descripcion,
                       BigDecimal precio, CategoriaProducto categoria, List<IngredienteDTO> ingredientes) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.ingredientes = ingredientes;
    }

    // Getters y Setters COMPLETOS
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public CategoriaProducto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProducto categoria) { this.categoria = categoria; }

    public List<IngredienteDTO> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<IngredienteDTO> ingredientes) { this.ingredientes = ingredientes; }
}