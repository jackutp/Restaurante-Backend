package com.microservicio.Mermas.Services;

import com.microservicio.Mermas.Entities.TipoMerma;
import com.microservicio.Mermas.dto.InsumoDTO;
import com.microservicio.Mermas.dto.MermaDTO;
import com.microservicio.Mermas.dto.MermaRequestDTO;
import com.microservicio.Mermas.dto.ProductoDTO;

import java.util.List;
import java.util.Optional;

public interface MermaService {
    List<MermaDTO> findAll();
    Optional<MermaDTO> findById(Integer id);
    MermaDTO save(MermaRequestDTO mermaDTO);
    MermaDTO update(Integer id, MermaRequestDTO mermaDTO);
    void delete(Integer id);
    List<MermaDTO> findByTipo(TipoMerma tipo);

    // Métodos para obtener datos de otros microservicios
    List<ProductoDTO> getProductos();
    List<InsumoDTO> getInsumos();
}