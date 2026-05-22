package com.microservicio.mesas.mapper;
import com.microservicio.mesas.dto.MesaResponseDTO;
import com.microservicio.mesas.entity.Mesa;
import org.springframework.stereotype.Component;
@Component
public class MesaMapper {

    public MesaResponseDTO toResponseDTO(Mesa mesa) {
        if (mesa == null) return null;

        return MesaResponseDTO.builder()
                .id(mesa.getId())
                .numero(mesa.getNumero())
                .capacidad(mesa.getCapacidad())
                .estado(mesa.getEstado())
                .totalActual(mesa.getTotalActual())
                .ordenActualId(mesa.getOrdenActualId())
                .build();
    }
}
