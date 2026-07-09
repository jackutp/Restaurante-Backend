package com.microservicio.Proveedor.Services.proveedor;

import com.microservicio.Proveedor.Entities.Proveedor;
import com.microservicio.Proveedor.Mapper.ProveedorMapper;
import com.microservicio.Proveedor.Repositories.ProveedorRepository;
import com.microservicio.Proveedor.dto.ProveedorDTO;
import com.microservicio.Proveedor.dto.ProveedorRequestDTO;
import com.microservicio.Proveedor.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProveedorServiceWriteImp implements ProveedorServiceWrite {
    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    private ProveedorMapper proveedorMapper;

    @Override
    @Transactional
    public ProveedorDTO save(ProveedorRequestDTO proveedorDTO) {
        log.info("Registrando proveedor '{}'", proveedorDTO.getNombre());
        Proveedor proveedor = proveedorMapper.toEntity(proveedorDTO);
        Proveedor saved = proveedorRepository.save(proveedor);
        log.info("Proveedor '{}' registrado correctamente (id={})", saved.getNombre(), saved.getProveedorid());
        return proveedorMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ProveedorDTO update(Integer id, ProveedorRequestDTO proveedorDTO) {
        log.info("Actualizando proveedor {}", id);
        Proveedor existingProveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));

        existingProveedor.setNombre(proveedorDTO.getNombre());
        existingProveedor.setDescripcion(proveedorDTO.getDescripcion());
        existingProveedor.setRuc(proveedorDTO.getRuc());
        existingProveedor.setRazonSocial(proveedorDTO.getRazonSocial());
        existingProveedor.setDireccionFiscal(proveedorDTO.getDireccionFiscal());

        Proveedor updated = proveedorRepository.save(existingProveedor);
        log.info("Proveedor {} actualizado correctamente", updated.getProveedorid());
        return proveedorMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando proveedor {}", id);
        if (!proveedorRepository.existsById(id)) {
            log.warn("Intento de eliminar proveedor inexistente {}", id);
            throw new ResourceNotFoundException("Proveedor no encontrado con id: " + id);
        }
        proveedorRepository.deleteById(id);
        log.info("Proveedor {} eliminado correctamente", id);
    }
}
