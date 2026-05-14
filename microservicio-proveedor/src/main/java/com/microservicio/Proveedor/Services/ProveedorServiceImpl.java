package com.microservicio.Proveedor.Services;

import com.microservicio.Proveedor.Entities.Proveedor;
import com.microservicio.Proveedor.Repositories.ProveedorRepository;
import com.microservicio.Proveedor.dto.ProveedorDTO;
import com.microservicio.Proveedor.dto.ProveedorRequestDTO;
import com.microservicio.Proveedor.Mapper.ProveedorMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository, ProveedorMapper proveedorMapper) {
        this.proveedorRepository = proveedorRepository;
        this.proveedorMapper = proveedorMapper;
    }

    @Override
    public List<ProveedorDTO> findAll() {
        return proveedorRepository.findAll()
                .stream()
                .map(proveedorMapper::toDTO)
                .collect(Collectors.toList());
    }

        @Override
        public Optional<ProveedorDTO> findById(Integer id) {
            return proveedorRepository.findById(id)
                    .map(proveedorMapper::toDTO);
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