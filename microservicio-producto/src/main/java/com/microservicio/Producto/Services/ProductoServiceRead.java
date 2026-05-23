package com.microservicio.Producto.Services;

import com.microservicio.Producto.Entities.Categoria;
import com.microservicio.Producto.dto.ProductoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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
