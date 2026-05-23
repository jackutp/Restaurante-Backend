package com.microservicio.Proveedor.Services.proveedor;

import com.microservicio.Proveedor.dto.ProveedorDTO;
import com.microservicio.Proveedor.dto.ProveedorRequestDTO;

import java.util.List;
import java.util.Optional;

public interface ProveedorServiceRead {
    List<ProveedorDTO> findAll();
    ProveedorDTO findById(Integer id);
}
