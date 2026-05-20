package com.microservicio.Insumos.Services;

import com.microservicio.Insumos.Entities.EstadoInsumo;
import com.microservicio.Insumos.Mapper.InsumoMapper;
import com.microservicio.Insumos.Repositories.InsumoRepository;
import com.microservicio.Insumos.dto.InsumoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InsumoServiceReadImp implements InsumoServiceRead{
    private final InsumoRepository insumoRepository;
    private final InsumoMapper insumoMapper;

    public InsumoServiceReadImp(InsumoRepository insumoRepository, InsumoMapper insumoMapper) {
        this.insumoRepository = insumoRepository;
        this.insumoMapper = insumoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoDTO> findAll() {
        return insumoRepository.findAll()
                .stream()
                .map(insumoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InsumoDTO> findById(Integer id) {
        return insumoRepository.findById(id)
                .map(insumoMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoDTO> findByEstado(EstadoInsumo estado) {
        return insumoRepository.findByEstadoInsumo(estado)
                .stream()
                .map(insumoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoDTO> findLowStock() {
        return insumoRepository.findLowStock()
                .stream()
                .map(insumoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoDTO> findOutOfStock() {
        return insumoRepository.findOutOfStock()
                .stream()
                .map(insumoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoDTO> findByNombre(String nombre) {
        return insumoRepository.searchByNombre(nombre)
                .stream()
                .map(insumoMapper::toDTO)
                .collect(Collectors.toList());
    }
}
