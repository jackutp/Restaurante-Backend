package com.microservicio.inventario.Services;

import com.microservicio.inventario.Entities.EstadoIngrediente;
import com.microservicio.inventario.Entities.Ingrediente;
import com.microservicio.inventario.Mapper.InventarioMapper;
import com.microservicio.inventario.Repositories.IngredienteRepository;
import com.microservicio.inventario.dto.CrearIngredienteDTO;
import com.microservicio.inventario.dto.IngredienteDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredienteServiceImpl implements IngredienteService {

    private final IngredienteRepository repository;
    private final InventarioMapper mapper;

    public IngredienteServiceImpl(IngredienteRepository repository, InventarioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredienteDTO> findAll() {
        List<Ingrediente> ingredientes = (List<Ingrediente>) repository.findAll();
        return ingredientes.stream()
                .map(mapper::toIngredienteDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IngredienteDTO> findById(Long id) {
        return repository.findById(id).map(mapper::toIngredienteDTO);
    }

    @Override
    @Transactional
    public IngredienteDTO save(CrearIngredienteDTO ingredienteDTO) {
        Ingrediente ingrediente = Ingrediente.builder()
                .nombre(ingredienteDTO.getNombre())
                .unidad(ingredienteDTO.getUnidad())
                .stock(ingredienteDTO.getStock())
                .estado(ingredienteDTO.getEstado())
                .build();

        // Asigna estado inicial según stock
        if (ingrediente.getStock() == 0) {
            ingrediente.setEstado(EstadoIngrediente.VACIO);
        } else if (ingrediente.getEstado() == null) {
            ingrediente.setEstado(EstadoIngrediente.DISPONIBLE);
        }

        Ingrediente saved = repository.save(ingrediente);
        return mapper.toIngredienteDTO(saved);
    }

    @Override
    @Transactional
    public Optional<IngredienteDTO> update(Long id, CrearIngredienteDTO ingredienteDTO) {
        return repository.findById(id).map(existing -> {
            existing.setNombre(ingredienteDTO.getNombre());
            existing.setUnidad(ingredienteDTO.getUnidad());
            existing.setStock(ingredienteDTO.getStock());
            existing.setEstado(ingredienteDTO.getEstado());
            return mapper.toIngredienteDTO(repository.save(existing));
        });
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return repository.findById(id).map(i -> {
            repository.delete(i);
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional
    public Optional<IngredienteDTO> reducirStock(Long id, Integer cantidad) {
        return repository.findById(id).map(i -> {
            i.reducirStock(cantidad);
            return mapper.toIngredienteDTO(repository.save(i));
        });
    }

    @Override
    @Transactional
    public Optional<IngredienteDTO> aumentarStock(Long id, Integer cantidad) {
        return repository.findById(id).map(i -> {
            i.aumentarStock(cantidad);
            return mapper.toIngredienteDTO(repository.save(i));
        });
    }
}


/*
package com.microservicio.inventario.Services;

import com.microservicio.inventario.Entities.EstadoIngrediente;
import com.microservicio.inventario.Entities.Ingrediente;
import com.microservicio.inventario.Repositories.IngredienteRepository;
import com.microservicio.inventario.dto.IngredienteDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class IngredienteServiceImpl implements IngredienteService {

    final private IngredienteRepository repository;

    public IngredienteServiceImpl(IngredienteRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingrediente> findAll() {
        return (List<Ingrediente>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ingrediente> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Ingrediente save(Ingrediente ingrediente) {
        // Asigna estado inicial según stock
        if (ingrediente.getStock() == 0) {
            ingrediente.setEstado(EstadoIngrediente.VACIO);
        } else if (ingrediente.getEstado() == null) {
            ingrediente.setEstado(EstadoIngrediente.DISPONIBLE);
        }
        return repository.save(ingrediente);
    }

    @Override
    @Transactional
    public Optional<Ingrediente> update(Long id, Ingrediente ingrediente) {
        return repository.findById(id).map(existing -> {
            existing.setNombre(ingrediente.getNombre());
            existing.setUnidad(ingrediente.getUnidad());
            existing.setStock(ingrediente.getStock());
            existing.setEstado(ingrediente.getEstado());
            return repository.save(existing);
        });
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return repository.findById(id).map(i -> {
            repository.delete(i);
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional
    public Optional<Ingrediente> reducirStock(Long id, Integer cantidad) {
        return repository.findById(id).map(i -> {
            i.reducirStock(cantidad);
            return repository.save(i);
        });
    }

    @Override
    @Transactional
    public Optional<Ingrediente> aumentarStock(Long id, Integer cantidad) {
        return repository.findById(id).map(i -> {
            i.aumentarStock(cantidad);
            return repository.save(i);
        });
    }
}

 */