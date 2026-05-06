package com.microservicio.inventario.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
public class MermaDTO {
    private Long mermaInsumoId;
    private String motivo;
    private LocalDate fecha;
    private Long ingredienteId;

    // Constructores
    public MermaDTO() {}

    public MermaDTO(Long mermaInsumoId, String motivo, LocalDate fecha, Long ingredienteId) {
        this.mermaInsumoId = mermaInsumoId;
        this.motivo = motivo;
        this.fecha = fecha;
        this.ingredienteId = ingredienteId;
    }

    // Getters y Setters
    public Long getMermaInsumoId() { return mermaInsumoId; }
    public void setMermaInsumoId(Long mermaInsumoId) { this.mermaInsumoId = mermaInsumoId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Long getIngredienteId() { return ingredienteId; }
    public void setIngredienteId(Long ingredienteId) { this.ingredienteId = ingredienteId; }
}