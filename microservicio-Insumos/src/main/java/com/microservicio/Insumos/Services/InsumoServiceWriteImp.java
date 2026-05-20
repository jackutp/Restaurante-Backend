package com.microservicio.Insumos.Services;

import com.microservicio.Insumos.dto.InsumoDTO;
import com.microservicio.Insumos.dto.InsumoRequestDTO;
import org.springframework.transaction.annotation.Transactional;

public class InsumoServiceWriteImp implements InsumoServiceWrite{
    @Override
    public InsumoDTO save(InsumoRequestDTO insumoDTO) {
        return null;
    }

    @Override
    public InsumoDTO update(Integer id, InsumoRequestDTO insumoDTO) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public InsumoDTO updateStock(Integer id, Integer nuevoStock) {
        return null;
    }
}
