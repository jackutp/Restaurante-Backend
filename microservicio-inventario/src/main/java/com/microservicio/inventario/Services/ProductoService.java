package com.microservicio.inventario.Services;

import com.microservicio.inventario.Entities.Producto;
import com.microservicio.inventario.dto.CrearProductoDTO;
import com.microservicio.inventario.dto.ProductoDTO;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<ProductoDTO> findAll();
    Optional<ProductoDTO> findById(Long id);
    ProductoDTO save(CrearProductoDTO producto);
    Optional<ProductoDTO> update(Long id, CrearProductoDTO producto);
    boolean deleteById(Long id);
}
/*
package com.microservicio.inventario.Services;

import com.microservicio.inventario.Entities.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> findAll();
    Optional<Producto> findById(Long id);
    Producto save(Producto producto);
    Optional<Producto> update(Long id, Producto producto);
    boolean deleteById(Long id);
}

 */