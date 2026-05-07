package com.microservicio.inventario.Services;

import com.microservicio.inventario.dto.CrearMermaDTO;
import com.microservicio.inventario.dto.MermaDTO;
import java.util.List;
import java.util.Optional;

public interface MermaInsumosService {
    List<MermaDTO> findAll();
    Optional<MermaDTO> findById(Long id);
    Optional<MermaDTO> save(Long ingredienteId, CrearMermaDTO merma);
    List<MermaDTO> findByIngrediente(Long ingredienteId);
    boolean deleteById(Long id);
}

/*
package com.microservicio.inventario.Services;

import com.microservicio.inventario.Entities.MermaInsumos;
import java.util.List;
import java.util.Optional;

public interface MermaInsumosService {
    List<MermaInsumos> findAll();
    Optional<MermaInsumos> findById(Long id);
    Optional<MermaInsumos> save(Long ingredienteId, MermaInsumos merma);
    List<MermaInsumos> findByIngrediente(Long ingredienteId);
    boolean deleteById(Long id);
}

 */