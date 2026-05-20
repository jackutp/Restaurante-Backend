package com.microservicio.Mermas.Services;

import com.microservicio.Mermas.Entities.Merma;
import com.microservicio.Mermas.Mapper.MermaMapper;
import com.microservicio.Mermas.Repositories.MermaRepository;
import com.microservicio.Mermas.Services.feign.InsumoFeignClient;
import com.microservicio.Mermas.Services.feign.ProductoFeignClient;
import com.microservicio.Mermas.dto.MermaDTO;
import com.microservicio.Mermas.dto.MermaRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MermaServiceWriteImp implements MermaServiceWrite{

    private final MermaRepository mermaRepository;
    private final MermaMapper mermaMapper;
    private final ProductoFeignClient productoFeignClient;
    private final InsumoFeignClient insumoFeignClient;

    public MermaServiceWriteImp(MermaRepository mermaRepository,
                            MermaMapper mermaMapper,
                            ProductoFeignClient productoFeignClient,
                            InsumoFeignClient insumoFeignClient) {
        this.mermaRepository = mermaRepository;
        this.mermaMapper = mermaMapper;
        this.productoFeignClient = productoFeignClient;
        this.insumoFeignClient = insumoFeignClient;
    }

    @Override
    @Transactional
    public MermaDTO save(MermaRequestDTO mermaDTO) {
        Merma merma = mermaMapper.toEntity(mermaDTO);
        Merma saved = mermaRepository.save(merma);
        return mermaMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public MermaDTO update(Integer id, MermaRequestDTO mermaDTO) {
        Merma existingMerma = mermaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Merma no encontrada con id: " + id));

        existingMerma.setTipoMerma(mermaDTO.getTipoMerma());
        existingMerma.setNombreMerma(mermaDTO.getNombreMerma());
        existingMerma.setCantidad(mermaDTO.getCantidad());
        existingMerma.setMotivo(mermaDTO.getMotivo());
        existingMerma.setReferenciaId(mermaDTO.getReferenciaId());
        existingMerma.setUnidadMedida(mermaDTO.getUnidadMedida());

        Merma updated = mermaRepository.save(existingMerma);
        return mermaMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!mermaRepository.existsById(id)) {
            throw new EntityNotFoundException("Merma no encontrada con id: " + id);
        }
        mermaRepository.deleteById(id);
    }
}
