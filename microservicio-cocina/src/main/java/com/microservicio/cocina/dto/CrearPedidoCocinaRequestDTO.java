package com.microservicio.cocina.dto;

import java.util.List;

public class CrearPedidoCocinaRequestDTO {
    private String ordenId;
    private Integer mesaNumero;
    private String hora;
    private List<ItemCocinaRequestDTO> items;

    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }
    public Integer getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(Integer mesaNumero) { this.mesaNumero = mesaNumero; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public List<ItemCocinaRequestDTO> getItems() { return items; }
    public void setItems(List<ItemCocinaRequestDTO> items) { this.items = items; }
}