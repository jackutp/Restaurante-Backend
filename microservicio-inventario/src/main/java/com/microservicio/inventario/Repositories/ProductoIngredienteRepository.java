package com.microservicio.inventario.Repositories;

import com.microservicio.inventario.Entities.ProductoIngrediente;
import com.microservicio.inventario.Entities.ProductoIngredienteId;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ProductoIngredienteRepository extends CrudRepository<ProductoIngrediente, ProductoIngredienteId> {
    List<ProductoIngrediente> findByProductoProductoId(Long productoId);
    List<ProductoIngrediente> findByIngredienteIngredienteId(Long ingredienteId);
}