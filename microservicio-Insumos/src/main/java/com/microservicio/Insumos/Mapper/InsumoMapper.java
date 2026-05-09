package com.microservicio.Insumos.Mapper;

import com.microservicio.Insumos.Entities.Insumos;
import com.microservicio.Insumos.dto.InsumoDTO;
import com.microservicio.Insumos.dto.InsumoRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class InsumoMapper {

    public InsumoDTO toDTO(Insumos insumo) {
        if (insumo == null) return null;

        InsumoDTO dto = new InsumoDTO();
        dto.setInsumoid(insumo.getInsumoid());
        dto.setNombre(insumo.getNombre());
        dto.setUnidadMedida(insumo.getUnidadMedida());
        dto.setStock(insumo.getStock());
        dto.setEstadoInsumo(insumo.getEstadoInsumo());

        return dto;
    }

    public Insumos toEntity(InsumoRequestDTO dto) {
        if (dto == null) return null;

        Insumos insumo = new Insumos();
        insumo.setNombre(dto.getNombre());
        insumo.setUnidadMedida(dto.getUnidadMedida());
        insumo.setStock(dto.getStock());
        insumo.actualizarEstado();

        return insumo;
    }

    public void updateEntity(Insumos insumo, InsumoRequestDTO dto) {
        if (dto.getNombre() != null) insumo.setNombre(dto.getNombre());
        if (dto.getUnidadMedida() != null) insumo.setUnidadMedida(dto.getUnidadMedida());
        if (dto.getStock() != null) {
            insumo.setStock(dto.getStock());
            insumo.actualizarEstado();
        }
    }
}