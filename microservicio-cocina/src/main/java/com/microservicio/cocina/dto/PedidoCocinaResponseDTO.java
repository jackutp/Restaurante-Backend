package com.microservicio.cocina.dto;

import java.util.List;

public class PedidoCocinaResponseDTO {
    private Long id;
    private String ordenId;
    private Integer mesaNumero;
    private String hora;
    private String estado;
    private List<ItemCocinaResponseDTO> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }
    public Integer getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(Integer mesaNumero) { this.mesaNumero = mesaNumero; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<ItemCocinaResponseDTO> getItems() { return items; }
    public void setItems(List<ItemCocinaResponseDTO> items) { this.items = items; }
}