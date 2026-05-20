package com.microservicio.Insumos.Services;

import com.microservicio.Insumos.dto.InsumoDTO;
import com.microservicio.Insumos.dto.InsumoRequestDTO;

public interface InsumoServiceWrite {
    InsumoDTO save(InsumoRequestDTO insumoDTO);
    InsumoDTO update(Integer id, InsumoRequestDTO insumoDTO);
    void delete(Integer id);
    InsumoDTO updateStock(Integer id, Integer nuevoStock);
}
