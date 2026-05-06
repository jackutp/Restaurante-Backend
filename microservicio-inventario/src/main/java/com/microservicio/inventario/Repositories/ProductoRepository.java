package com.microservicio.inventario.Repositories;

import com.microservicio.inventario.Entities.Producto;
import org.springframework.data.repository.CrudRepository;
public interface ProductoRepository extends CrudRepository<Producto,Long> {
}
