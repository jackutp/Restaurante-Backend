package com.microservicio.Insumos.Services;

import com.microservicio.Insumos.Entities.Insumos;
import com.microservicio.Insumos.Mapper.InsumoMapper;
import com.microservicio.Insumos.Repositories.InsumoRepository;
import com.microservicio.Insumos.dto.InsumoDTO;
import com.microservicio.Insumos.dto.InsumoRequestDTO;
import com.microservicio.Insumos.exception.ConflictException;
import com.microservicio.Insumos.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InsumoServiceWriteImp implements InsumoServiceWrite{
    @Autowired
    private  InsumoRepository insumoRepository;
    @Autowired
    private  InsumoMapper insumoMapper;
    @Autowired
    private  InsumoServiceRead insumoRead;

    @Override
    @Transactional
    public InsumoDTO save(InsumoRequestDTO insumoDTO) {
        if(insumoRepository.findByNombre(insumoDTO.getNombre()).isPresent()){
           throw new ConflictException("Ya existe un insumo con el nombre: " + insumoDTO.getNombre());
        }
        Insumos insumo = insumoMapper.toEntity(insumoDTO);
        Insumos saved = insumoRepository.save(insumo);
        return insumoMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public InsumoDTO update(Integer id, InsumoRequestDTO insumoDTO) {
        Insumos existingInsumo = insumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + id));
       if(!existingInsumo.getNombre().equals(insumoDTO.getNombre())) {
           insumoRepository.findByNombre(insumoDTO.getNombre()).ifPresent(
                   i -> {
                       if(!i.getInsumoid().equals(id)){
                           throw new ConflictException("Ya existe otro insumo con el nombre" + insumoDTO.getNombre());
                       }
                   }
           );
       }
       insumoMapper.updateEntity(existingInsumo, insumoDTO);
       Insumos updated = insumoRepository.save(existingInsumo);
        return insumoMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if(!insumoRepository.existsById(id)){
            throw new ResourceNotFoundException("Insumo no encontrado con id: " + id);
        }
        insumoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public InsumoDTO updateStock(Integer id, Integer nuevoStock) {
        if(nuevoStock < 0){
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        int updated = insumoRepository.updateStock(id, nuevoStock);
        if(updated == 0){
            throw new ResourceNotFoundException("Insumo no encontrado con id: " + id);
        }
        return insumoRead.findById(id).orElseThrow(() ->  new ResourceNotFoundException("Insumo no encontrado con id: " + id));
    }
}
