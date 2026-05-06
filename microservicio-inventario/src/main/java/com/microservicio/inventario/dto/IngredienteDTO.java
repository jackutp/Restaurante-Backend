package com.microservicio.inventario.dto;

import com.microservicio.inventario.entity.EstadoIngrediente;
import com.microservicio.inventario.entity.UnidadMedida;
import java.util.List;

public record IngredienteDTO(
        Long ingredienteId,
        String nombre,
        UnidadMedida unidad,
        Integer stock,
        EstadoIngrediente estado,
        List<MermaDTO> mermas
) {}