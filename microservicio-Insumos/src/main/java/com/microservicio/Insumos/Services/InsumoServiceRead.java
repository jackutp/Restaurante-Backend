package com.microservicio.Insumos.Services;

import com.microservicio.Insumos.Entities.EstadoInsumo;
import com.microservicio.Insumos.dto.InsumoDTO;
import com.microservicio.Insumos.dto.InsumoRequestDTO;

import java.util.List;
import java.util.Optional;

public interface InsumoServiceRead {
    List<InsumoDTO> findAll();
    Optional<InsumoDTO> findById(Integer id);
    List<InsumoDTO> findByEstado(EstadoInsumo estado);
    List<InsumoDTO> findLowStock();
    List<InsumoDTO> findOutOfStock();
    List<InsumoDTO> findByNombre(String nombre);
}
