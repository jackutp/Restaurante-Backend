package com.microservicio.Mermas.Mapper;

import com.microservicio.Mermas.Entities.Merma;
import com.microservicio.Mermas.dto.MermaDTO;
import com.microservicio.Mermas.dto.MermaRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class MermaMapper {

    public MermaDTO toDTO(Merma merma) {
        if (merma == null) return null;

        MermaDTO dto = new MermaDTO();
        dto.setMermaid(merma.getMermaid());
        dto.setTipoMerma(merma.getTipoMerma());
        dto.setNombreMerma(merma.getNombreMerma());
        dto.setCantidad(merma.getCantidad());
        dto.setMotivo(merma.getMotivo());
        dto.setFecha(merma.getFecha());
        dto.setReferenciaId(merma.getReferenciaId());
        dto.setUnidadMedida(merma.getUnidadMedida());

        return dto;
    }

    public Merma toEntity(MermaRequestDTO dto) {
        if (dto == null) return null;

        Merma merma = new Merma();
        merma.setTipoMerma(dto.getTipoMerma());
        merma.setNombreMerma(dto.getNombreMerma());
        merma.setCantidad(dto.getCantidad());
        merma.setMotivo(dto.getMotivo());
        merma.setReferenciaId(dto.getReferenciaId());
        merma.setUnidadMedida(dto.getUnidadMedida());

        return merma;
    }
}