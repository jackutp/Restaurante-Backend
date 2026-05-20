package com.microservicio.pedidos.dto;

import com.microservicio.pedidos.entity.EstadoPedido;
import java.util.List;

public class PedidoResponseDTO {
    private Long id;
    private String ordenId;
    private Integer mesaNumero;
    private String hora;
    private EstadoPedido estado;
    private List<PedidoItemResponseDTO> items;

    private PedidoResponseDTO(Builder builder) {
        this.id = builder.id;
        this.ordenId = builder.ordenId;
        this.mesaNumero = builder.mesaNumero;
        this.hora = builder.hora;
        this.estado = builder.estado;
        this.items = builder.items;
    }

    // Getters
    public Long getId() { return id; }
    public String getOrdenId() { return ordenId; }
    public Integer getMesaNumero() { return mesaNumero; }
    public String getHora() { return hora; }
    public EstadoPedido getEstado() { return estado; }
    public List<PedidoItemResponseDTO> getItems() { return items; }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String ordenId;
        private Integer mesaNumero;
        private String hora;
        private EstadoPedido estado;
        private List<PedidoItemResponseDTO> items;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder ordenId(String ordenId) { this.ordenId = ordenId; return this; }
        public Builder mesaNumero(Integer mesaNumero) { this.mesaNumero = mesaNumero; return this; }
        public Builder hora(String hora) { this.hora = hora; return this; }
        public Builder estado(EstadoPedido estado) { this.estado = estado; return this; }
        public Builder items(List<PedidoItemResponseDTO> items) { this.items = items; return this; }

        public PedidoResponseDTO build() {
            return new PedidoResponseDTO(this);
        }
    }
}