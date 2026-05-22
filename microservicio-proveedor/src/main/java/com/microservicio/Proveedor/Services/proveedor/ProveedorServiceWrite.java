package com.microservicio.Proveedor.Services.proveedor;

import com.microservicio.Proveedor.dto.ProveedorDTO;
import com.microservicio.Proveedor.dto.ProveedorRequestDTO;

public interface ProveedorServiceWrite {
    ProveedorDTO save(ProveedorRequestDTO proveedorDTO);
    ProveedorDTO update(Integer id, ProveedorRequestDTO proveedorDTO);
    void delete(Integer id);
}
