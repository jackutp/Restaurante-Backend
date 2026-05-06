package com.microservicio.inventario.Repositories;

import com.microservicio.inventario.Entities.MermaInsumos;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface MermaInsumosRepository extends CrudRepository<MermaInsumos, Long> {
    List<MermaInsumos> findByIngredienteIngredienteId(Long ingredienteId);
}