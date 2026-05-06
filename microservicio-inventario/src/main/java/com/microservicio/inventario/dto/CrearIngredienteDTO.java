package com.microservicio.inventario.dto;

import com.microservicio.inventario.Entities.EstadoIngrediente;
import com.microservicio.inventario.Entities.UnidadMedida;

public record CrearIngredienteDTO(
        String nombre,
        UnidadMedida unidad,
        Integer stock,
        EstadoIngrediente estado
) {}