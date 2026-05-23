package com.microservicio.Proveedor.Services.proveedor;

import com.microservicio.Proveedor.Mapper.ProveedorMapper;
import com.microservicio.Proveedor.Repositories.ProveedorRepository;
import com.microservicio.Proveedor.dto.ProveedorDTO;
import com.microservicio.Proveedor.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProveedorServiceReadImp implements ProveedorServiceRead{
    @Autowired
    private  ProveedorRepository proveedorRepository;
    @Autowired
    private  ProveedorMapper proveedorMapper;

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
    public ProveedorDTO findById(Integer id) {
        return proveedorRepository.findById(id).map(proveedorMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No se enconctró proveedor con el id: " + id));
    }
}
