package com.microservicio.Proveedor.Services;

import com.microservicio.Proveedor.dto.ProveedorDTO;
import com.microservicio.Proveedor.dto.ProveedorRequestDTO;
import java.util.List;
import java.util.Optional;

public interface ProveedorService {
    List<ProveedorDTO> findAll();
    Optional<ProveedorDTO> findById(Integer id);
    ProveedorDTO save(ProveedorRequestDTO proveedorDTO);
    ProveedorDTO update(Integer id, ProveedorRequestDTO proveedorDTO);
    void delete(Integer id);
}