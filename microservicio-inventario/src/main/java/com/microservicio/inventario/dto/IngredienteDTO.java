package com.microservicio.inventario.dto;

import com.microservicio.inventario.Entities.EstadoIngrediente;
import com.microservicio.inventario.Entities.UnidadMedida;
import lombok.Builder;
import lombok.Data;

import java.util.List;
// En IngredienteDTO.java
@Data
@Builder
public class IngredienteDTO {
    private Long ingredienteId;
    private String nombre;
    private UnidadMedida unidad;
    private Integer stock;
    private EstadoIngrediente estado;
    private List<MermaDTO> mermas;

    // Constructores
    public IngredienteDTO() {}

    public IngredienteDTO(Long ingredienteId, String nombre, UnidadMedida unidad,
                          Integer stock, EstadoIngrediente estado, List<MermaDTO> mermas) {
        this.ingredienteId = ingredienteId;
        this.nombre = nombre;
        this.unidad = unidad;
        this.stock = stock;
        this.estado = estado;
        this.mermas = mermas;
    }

    // Getters y Setters
    public Long getIngredienteId() { return ingredienteId; }
    public void setIngredienteId(Long ingredienteId) { this.ingredienteId = ingredienteId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public UnidadMedida getUnidad() { return unidad; }
    public void setUnidad(UnidadMedida unidad) { this.unidad = unidad; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public EstadoIngrediente getEstado() { return estado; }
    public void setEstado(EstadoIngrediente estado) { this.estado = estado; }

    public List<MermaDTO> getMermas() { return mermas; }
    public void setMermas(List<MermaDTO> mermas) { this.mermas = mermas; }
}