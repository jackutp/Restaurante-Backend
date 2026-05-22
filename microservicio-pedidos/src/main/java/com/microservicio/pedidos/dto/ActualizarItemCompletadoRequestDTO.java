package com.microservicio.pedidos.dto;

import jakarta.validation.constraints.NotNull;

public class ActualizarItemCompletadoRequestDTO {

    @NotNull(message = "El estado completado es obligatorio")
    private Boolean completado;

    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
}