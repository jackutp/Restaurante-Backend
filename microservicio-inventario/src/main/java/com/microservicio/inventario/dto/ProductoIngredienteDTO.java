package com.microservicio.inventario.dto;

public record ProductoIngredienteDTO(
        Long productoId,
        Long ingredienteId,
        Integer cantidad
) {}