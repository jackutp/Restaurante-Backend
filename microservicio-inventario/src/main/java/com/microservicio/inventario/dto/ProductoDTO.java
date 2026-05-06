package com.microservicio.inventario.dto;

import com.microservicio.inventario.entity.CategoriaProducto;
import java.math.BigDecimal;
import java.util.List;

public record ProductoDTO(
        Long productoId,
        String nombre,
        String descripcion,
        BigDecimal precio,
        CategoriaProducto categoria,
        List<IngredienteDTO> ingredientes  // Simplificado
) {}