package com.microservicio.Insumos.Services;

import com.microservicio.Insumos.Entities.Insumos;
import com.microservicio.Insumos.Mapper.InsumoMapper;
import com.microservicio.Insumos.Repositories.InsumoRepository;
import com.microservicio.Insumos.dto.InsumoDTO;
import com.microservicio.Insumos.dto.InsumoRequestDTO;
import com.microservicio.Insumos.exception.ConflictException;
import com.microservicio.Insumos.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class InsumoServiceWriteImp implements InsumoServiceWrite {
    @Autowired
    private InsumoRepository insumoRepository;
    @Autowired
    private InsumoMapper insumoMapper;
    @Autowired
    private InsumoServiceRead insumoRead;

    @Override
    @Transactional
    public InsumoDTO save(InsumoRequestDTO insumoDTO) {
        log.info("Registrando insumo '{}' ", insumoDTO.getNombre());
        if (insumoRepository.findByNombre(insumoDTO.getNombre()).isPresent()) {
            log.warn("Se intentó registrar un insumo ya existente: {}", insumoDTO.getNombre());
            throw new ConflictException("Ya existe un insumo con el nombre: " + insumoDTO.getNombre());
        }
        Insumos insumo = insumoMapper.toEntity(insumoDTO);
        Insumos saved = insumoRepository.save(insumo);
        log.info("Insumo {} registrado correctamente (id={})", saved.getNombre(), saved.getInsumoid());
        return insumoMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public InsumoDTO update(Integer id, InsumoRequestDTO insumoDTO) {
        log.info("Actualizando insumo con id={}", id);
        Insumos existingInsumo = insumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + id));
        if (!existingInsumo.getNombre().equals(insumoDTO.getNombre())) {
            insumoRepository.findByNombre(insumoDTO.getNombre()).ifPresent(
                    i -> {
                        if (!i.getInsumoid().equals(id)) {
                            log.warn("Se intentó renombrar un insumo {} con un nombre ya existente: {}", id, insumoDTO.getNombre());
                            throw new ConflictException("Ya existe otro insumo con el nombre" + insumoDTO.getNombre());
                        }
                    }
            );
        }
        insumoMapper.updateEntity(existingInsumo, insumoDTO);
        Insumos updated = insumoRepository.save(existingInsumo);
        log.info("Insumo {} actualizado correctamente (id={})", updated.getNombre(), updated.getInsumoid());
        return insumoMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando insumo {}", id);
        if (!insumoRepository.existsById(id)) {
            log.warn("Se intentó eliminar un insumo inexistente: {}", id);
            throw new ResourceNotFoundException("Insumo no encontrado con id: " + id);
        }
        insumoRepository.deleteById(id);
        log.info("Insumo {} eliminado", id);
    }

    @Override
    @Transactional
    public InsumoDTO updateStock(Integer id, Integer nuevoStock) {
        if (nuevoStock < 0) {
            log.warn("Se intentó actualizar el insumo {} a un stock negativo {}", id, nuevoStock);
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        int updated = insumoRepository.updateStock(id, nuevoStock);
        if (updated == 0) {
            log.warn("Se intentó actualizar un insumo inexistente: {}", id);
            throw new ResourceNotFoundException("Insumo no encontrado con id: " + id);
        }
        log.info("Stock del insumo {} cambiado a {}", id, nuevoStock);
        return insumoRead.findById(id).orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + id));
    }
}
