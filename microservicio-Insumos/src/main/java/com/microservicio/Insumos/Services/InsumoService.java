package com.microservicio.Insumos.Services;
import com.microservicio.Insumos.Entities.EstadoInsumo;
import com.microservicio.Insumos.dto.InsumoDTO;
import com.microservicio.Insumos.dto.InsumoRequestDTO;
import java.util.List;
import java.util.Optional;
public interface InsumoService {
    List<InsumoDTO> findAll();
    Optional<InsumoDTO> findById(Integer id);
    InsumoDTO save(InsumoRequestDTO insumoDTO);
    InsumoDTO update(Integer id, InsumoRequestDTO insumoDTO);
    void delete(Integer id);
    InsumoDTO updateStock(Integer id, Integer nuevoStock);
    List<InsumoDTO> findByEstado(EstadoInsumo estado);
    List<InsumoDTO> findLowStock();
    List<InsumoDTO> findOutOfStock();
    List<InsumoDTO> findByNombre(String nombre);
}