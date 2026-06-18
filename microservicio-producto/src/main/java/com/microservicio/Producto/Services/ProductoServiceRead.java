package com.microservicio.Producto.Services;

import com.microservicio.Producto.Entities.Categoria;
import com.microservicio.Producto.dto.ProductoDTO;

import java.util.List;

public interface ProductoServiceRead {
    // READ básico
    List<ProductoDTO> findAll();
    ProductoDTO findById(Integer id);
    // Métodos específicos para imágenes
    byte[] getImagen(Integer id);
    // Métodos de búsqueda
    List<ProductoDTO> findByCategoria(Categoria categoria);
    List<ProductoDTO> findByPrecioRange(Double min, Double max);
    Integer getStock(Integer id);
}
