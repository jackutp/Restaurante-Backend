
package com.microservicio.inventario.Services;

import com.microservicio.inventario.dto.CrearIngredienteDTO;
import com.microservicio.inventario.dto.IngredienteDTO;

import java.util.List;
import java.util.Optional;

public interface IngredienteService {
    List<IngredienteDTO> findAll();
    Optional<IngredienteDTO> findById(Long id);
    IngredienteDTO save(CrearIngredienteDTO ingrediente);
    Optional<IngredienteDTO> update(Long id, CrearIngredienteDTO ingrediente);
    boolean deleteById(Long id);
    Optional<IngredienteDTO> reducirStock(Long id, Integer cantidad);
    Optional<IngredienteDTO> aumentarStock(Long id, Integer cantidad);
}
/*
package com.microservicio.inventario.Services;

import com.microservicio.inventario.Entities.Ingrediente;
import com.microservicio.inventario.dto.IngredienteDTO;

import java.util.List;
import java.util.Optional;

public interface IngredienteService {
    List<Ingrediente> findAll();
    Optional<Ingrediente> findById(Long id);
    Ingrediente save(Ingrediente ingrediente);
    Optional<Ingrediente> update(Long id, Ingrediente ingrediente);
    boolean deleteById(Long id);
    Optional<Ingrediente> reducirStock(Long id, Integer cantidad);
    Optional<Ingrediente> aumentarStock(Long id, Integer cantidad);
}

 */