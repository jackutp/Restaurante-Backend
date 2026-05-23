package com.microservicio.pedidos.dto;
import com.microservicio.pedidos.entity.EstadoPedido;
import jakarta.validation.constraints.NotNull;
public class ActualizarEstadoRequestDTO {

    @NotNull(message = "El estado es obligatorio")
    private EstadoPedido estado;

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
}