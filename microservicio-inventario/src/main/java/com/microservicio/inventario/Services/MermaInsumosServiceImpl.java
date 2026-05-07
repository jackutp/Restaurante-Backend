package com.microservicio.inventario.Services;

import com.microservicio.inventario.Entities.MermaInsumos;
import com.microservicio.inventario.Mapper.InventarioMapper;
import com.microservicio.inventario.Repositories.IngredienteRepository;
import com.microservicio.inventario.Repositories.MermaInsumosRepository;
import com.microservicio.inventario.dto.CrearMermaDTO;
import com.microservicio.inventario.dto.MermaDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MermaInsumosServiceImpl implements MermaInsumosService {

    private final MermaInsumosRepository mermaRepository;
    private final IngredienteRepository ingredienteRepository;
    private final InventarioMapper mapper;

    public MermaInsumosServiceImpl(MermaInsumosRepository mermaRepository,
                                   IngredienteRepository ingredienteRepository,
                                   InventarioMapper mapper) {
        this.mermaRepository = mermaRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MermaDTO> findAll() {
        List<MermaInsumos> mermas = (List<MermaInsumos>) mermaRepository.findAll();
        return mermas.stream()
                .map(mapper::toMermaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MermaDTO> findById(Long id) {
        return mermaRepository.findById(id).map(mapper::toMermaDTO);
    }

    @Override
    @Transactional
    public Optional<MermaDTO> save(Long ingredienteId, CrearMermaDTO mermaDTO) {
        return ingredienteRepository.findById(ingredienteId).map(ingrediente -> {
            MermaInsumos merma = MermaInsumos.builder()
                    .motivo(mermaDTO.getMotivo())
                    .ingrediente(ingrediente)
                    .build();
            return mapper.toMermaDTO(mermaRepository.save(merma));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<MermaDTO> findByIngrediente(Long ingredienteId) {
        List<MermaInsumos> mermas = mermaRepository.findByIngredienteIngredienteId(ingredienteId);
        return mermas.stream()
                .map(mapper::toMermaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return mermaRepository.findById(id).map(m -> {
            mermaRepository.delete(m);
            return true;
        }).orElse(false);
    }
}
/*
package com.microservicio.inventario.Services;

import com.microservicio.inventario.Entities.MermaInsumos;
import com.microservicio.inventario.Repositories.IngredienteRepository;
import com.microservicio.inventario.Repositories.MermaInsumosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MermaInsumosServiceImpl implements MermaInsumosService {

    final private MermaInsumosRepository mermaRepository;
    final private IngredienteRepository ingredienteRepository;

    public MermaInsumosServiceImpl(MermaInsumosRepository mermaRepository,
                                   IngredienteRepository ingredienteRepository) {
        this.mermaRepository = mermaRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MermaInsumos> findAll() {
        return (List<MermaInsumos>) mermaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MermaInsumos> findById(Long id) {
        return mermaRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<MermaInsumos> save(Long ingredienteId, MermaInsumos merma) {
        return ingredienteRepository.findById(ingredienteId).map(ingrediente -> {
            merma.setIngrediente(ingrediente);
            return mermaRepository.save(merma);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<MermaInsumos> findByIngrediente(Long ingredienteId) {
        return mermaRepository.findByIngredienteIngredienteId(ingredienteId);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return mermaRepository.findById(id).map(m -> {
            mermaRepository.delete(m);
            return true;
        }).orElse(false);
    }
}

 */