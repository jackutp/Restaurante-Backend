package com.microservicio.Insumos.dto;
import com.microservicio.Insumos.Entities.UnidadMedida;
import com.microservicio.Insumos.Entities.EstadoInsumo;
import lombok.Data;
@Data
public class InsumoDTO {
    private Integer insumoid;
    private String nombre;
    private UnidadMedida unidadMedida;
    private Integer stock;
    private EstadoInsumo estadoInsumo;
}