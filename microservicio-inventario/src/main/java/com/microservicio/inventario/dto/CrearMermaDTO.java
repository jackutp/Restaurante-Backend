package com.microservicio.inventario.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrearMermaDTO {
    private String motivo;

    public CrearMermaDTO() {}

    public CrearMermaDTO(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}