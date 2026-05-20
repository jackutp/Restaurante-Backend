package com.microservicio.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CrearPedidoRequestDTO {

    @NotNull(message = "El número de mesa es obligatorio")
    @Min(value = 1, message = "La mesa debe ser mayor a 0")
    private Integer mesaNumero;

    @Valid
    @NotNull(message = "Los items son obligatorios")
    private List<PedidoItemRequestDTO> items;

    // Getters y Setters
    public Integer getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(Integer mesaNumero) { this.mesaNumero = mesaNumero; }

    public List<PedidoItemRequestDTO> getItems() { return items; }
    public void setItems(List<PedidoItemRequestDTO> items) { this.items = items; }

}