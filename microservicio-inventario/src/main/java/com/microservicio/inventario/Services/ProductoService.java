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