package com.microservicio.Proveedor.Services.proveedor;

import com.microservicio.Proveedor.Mapper.ProveedorMapper;
import com.microservicio.Proveedor.Repositories.ProveedorRepository;
import com.microservicio.Proveedor.dto.ProveedorDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProveedorServiceReadImp implements ProveedorServiceRead{
    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    public ProveedorServiceReadImp(ProveedorRepository proveedorRepository, ProveedorMapper proveedorMapper) {
        this.proveedorRepository = proveedorRepository;
        this.proveedorMapper = proveedorMapper;
    }
    @Override
    @Transactional(readOnly = true)
    public List<ProveedorDTO> findAll() {
        return proveedorRepository.findAll()
                .stream()
                .map(proveedorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProveedorDTO> findById(Integer id) {
        return proveedorRepository.findById(id)
                .map(proveedorMapper::toDTO);
    }
}
