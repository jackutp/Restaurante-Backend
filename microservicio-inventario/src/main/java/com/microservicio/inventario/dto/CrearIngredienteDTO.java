package com.microservicio.inventario.dto;

import com.microservicio.inventario.Entities.EstadoIngrediente;
import com.microservicio.inventario.Entities.UnidadMedida;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CrearIngredienteDTO {
    private String nombre;
    private UnidadMedida unidad;
    private Integer stock;
    private EstadoIngrediente estado;

    // Constructores
    public CrearIngredienteDTO() {}

    public CrearIngredienteDTO(String nombre, UnidadMedida unidad, Integer stock, EstadoIngrediente estado) {
        this.nombre = nombre;
        this.unidad = unidad;
        this.stock = stock;
        this.estado = estado;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public UnidadMedida getUnidad() { return unidad; }
    public void setUnidad(UnidadMedida unidad) { this.unidad = unidad; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public EstadoIngrediente getEstado() { return estado; }
    public void setEstado(EstadoIngrediente estado) { this.estado = estado; }
}