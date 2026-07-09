package com.microservicio.Mermas.Services;

import com.microservicio.Mermas.Entities.Merma;
import com.microservicio.Mermas.Mapper.MermaMapper;
import com.microservicio.Mermas.Repositories.MermaRepository;
import com.microservicio.Mermas.Services.feign.InsumoFeignClient;
import com.microservicio.Mermas.Services.feign.ProductoFeignClient;
import com.microservicio.Mermas.dto.MermaDTO;
import com.microservicio.Mermas.dto.MermaRequestDTO;
import com.microservicio.Mermas.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MermaServiceWriteImp implements MermaServiceWrite {

    @Autowired
    private MermaRepository mermaRepository;
    @Autowired
    private MermaMapper mermaMapper;

    @Override
    @Transactional
    public MermaDTO save(MermaRequestDTO mermaDTO) {
        log.info("Registrando merma '{}' de {} {}", mermaDTO.getNombreMerma(), mermaDTO.getCantidad(), mermaDTO.getUnidadMedida());
        Merma merma = mermaMapper.toEntity(mermaDTO);
        Merma saved = mermaRepository.save(merma);
        log.info("Merma {} registrada exitosamente (id={})", saved.getNombreMerma(), saved.getMermaid());
        return mermaMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public MermaDTO update(Integer id, MermaRequestDTO mermaDTO) {
        log.info("Actualizando merma con id {}", id);
        Merma existingMerma = mermaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merma no encontrada con id: " + id));

        existingMerma.setTipoMerma(mermaDTO.getTipoMerma());
        existingMerma.setNombreMerma(mermaDTO.getNombreMerma());
        existingMerma.setCantidad(mermaDTO.getCantidad());
        existingMerma.setMotivo(mermaDTO.getMotivo());
        existingMerma.setReferenciaId(mermaDTO.getReferenciaId());
        existingMerma.setUnidadMedida(mermaDTO.getUnidadMedida());

        Merma updated = mermaRepository.save(existingMerma);
        log.info("Merma {} actualizada exitosamente (id={})", updated.getNombreMerma(), updated.getMermaid());
        return mermaMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando merma con id {}", id);
        if (!mermaRepository.existsById(id)) {
            log.warn("Se intentó eliminar una merma inexistende (id={})", id);
            throw new ResourceNotFoundException("Merma no encontrada con id: " + id);
        }
        log.info("Merma {} eliminada correctamente", id);
        mermaRepository.deleteById(id);
    }
}
