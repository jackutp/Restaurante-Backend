package com.microservicio.Proveedor.Services.proveedor;

import com.microservicio.Proveedor.Entities.Proveedor;
import com.microservicio.Proveedor.Mapper.ProveedorMapper;
import com.microservicio.Proveedor.Repositories.ProveedorRepository;
import com.microservicio.Proveedor.dto.ProveedorDTO;
import com.microservicio.Proveedor.dto.ProveedorRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class ProveedorServiceWriteImp implements ProveedorServiceWrite{
    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    public ProveedorServiceWriteImp(ProveedorRepository proveedorRepository, ProveedorMapper proveedorMapper) {
        this.proveedorRepository = proveedorRepository;
        this.proveedorMapper = proveedorMapper;
    }

    @Override
    @Transactional
    public ProveedorDTO save(ProveedorRequestDTO proveedorDTO) {
        Proveedor proveedor = proveedorMapper.toEntity(proveedorDTO);
        Proveedor saved = proveedorRepository.save(proveedor);
        return proveedorMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ProveedorDTO update(Integer id, ProveedorRequestDTO proveedorDTO) {
        Proveedor existingProveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado con id: " + id));

        existingProveedor.setNombre(proveedorDTO.getNombre());
        existingProveedor.setDescripcion(proveedorDTO.getDescripcion());
        existingProveedor.setRuc(proveedorDTO.getRuc());
        existingProveedor.setRazonSocial(proveedorDTO.getRazonSocial());
        existingProveedor.setDireccionFiscal(proveedorDTO.getDireccionFiscal());

        Proveedor updated = proveedorRepository.save(existingProveedor);
        return proveedorMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!proveedorRepository.existsById(id)) {
            throw new EntityNotFoundException("Proveedor no encontrado con id: " + id);
        }
        proveedorRepository.deleteById(id);
    }
}
