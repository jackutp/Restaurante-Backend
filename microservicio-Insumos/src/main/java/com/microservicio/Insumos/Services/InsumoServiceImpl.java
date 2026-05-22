package com.microservicio.Insumos.Services;
import com.microservicio.Insumos.Entities.Insumos;
import com.microservicio.Insumos.Entities.EstadoInsumo;
import com.microservicio.Insumos.Repositories.InsumoRepository;
import com.microservicio.Insumos.dto.InsumoDTO;
import com.microservicio.Insumos.dto.InsumoRequestDTO;
import com.microservicio.Insumos.Mapper.InsumoMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class InsumoServiceImpl implements InsumoService {
    private final InsumoRepository insumoRepository;
    private final InsumoMapper insumoMapper;

    public InsumoServiceImpl(InsumoRepository insumoRepository, InsumoMapper insumoMapper) {
        this.insumoRepository = insumoRepository;
        this.insumoMapper = insumoMapper;
    }
    @Override
    public List<InsumoDTO> findAll() {
        return insumoRepository.findAll()
                .stream()
                .map(insumoMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    public Optional<InsumoDTO> findById(Integer id) {
        return insumoRepository.findById(id)
                .map(insumoMapper::toDTO);
    }
    @Override
    public List<InsumoDTO> findByNombre(String nombre) {
        return insumoRepository.searchByNombre(nombre)
                .stream()
                .map(insumoMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public InsumoDTO save(InsumoRequestDTO insumoDTO) {
        // Verificar nombre duplicado
        if (insumoRepository.findByNombre(insumoDTO.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un insumo con el nombre: " + insumoDTO.getNombre());
        }
        Insumos insumo = insumoMapper.toEntity(insumoDTO);
        Insumos saved = insumoRepository.save(insumo);
        return insumoMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public InsumoDTO update(Integer id, InsumoRequestDTO insumoDTO) {
        Insumos existingInsumo = insumoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo no encontrado con id: " + id));
        // Verificar nombre duplicado (si cambió el nombre)
        if (!existingInsumo.getNombre().equals(insumoDTO.getNombre())) {
            insumoRepository.findByNombre(insumoDTO.getNombre()).ifPresent(i -> {
                if (!i.getInsumoid().equals(id)) {
                    throw new RuntimeException("Ya existe otro insumo con el nombre: " + insumoDTO.getNombre());
                }
            });
        }

        insumoMapper.updateEntity(existingInsumo, insumoDTO);
        Insumos updated = insumoRepository.save(existingInsumo);
        return insumoMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!insumoRepository.existsById(id)) {
            throw new EntityNotFoundException("Insumo no encontrado con id: " + id);
        }
        insumoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public InsumoDTO updateStock(Integer id, Integer nuevoStock) {
        if (nuevoStock < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        int updated = insumoRepository.updateStock(id, nuevoStock);
        if (updated == 0) {
            throw new EntityNotFoundException("Insumo no encontrado con id: " + id);
        }

        return findById(id).orElseThrow(() -> new EntityNotFoundException("Insumo no encontrado con id: " + id));
    }

    @Override
    public List<InsumoDTO> findByEstado(EstadoInsumo estado) {
        return insumoRepository.findByEstadoInsumo(estado)
                .stream()
                .map(insumoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InsumoDTO> findLowStock() {
        return insumoRepository.findLowStock()
                .stream()
                .map(insumoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InsumoDTO> findOutOfStock() {
        return insumoRepository.findOutOfStock()
                .stream()
                .map(insumoMapper::toDTO)
                .collect(Collectors.toList());
    }
}