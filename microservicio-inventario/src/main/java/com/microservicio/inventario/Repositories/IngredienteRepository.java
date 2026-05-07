package com.microservicio.inventario.Repositories;

import com.microservicio.inventario.Entities.Ingrediente;
import org.springframework.data.repository.CrudRepository;

public interface IngredienteRepository extends CrudRepository<Ingrediente, Long> {
}