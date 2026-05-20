package com.microservicio.pedidos.dto;

public class PedidoItemResponseDTO {
    private Long id;
    private Integer productoId;
    private String nombre;
    private Double precio;
    private Integer cantidad;
    private String notas;
    private Boolean completado;

    private PedidoItemResponseDTO(Builder builder) {
        this.id = builder.id;
        this.productoId = builder.productoId;
        this.nombre = builder.nombre;
        this.precio = builder.precio;
        this.cantidad = builder.cantidad;
        this.notas = builder.notas;
        this.completado = builder.completado;
    }

    // Getters
    public Long getId() { return id; }
    public Integer getProductoId() { return productoId; }
    public String getNombre() { return nombre; }
    public Double getPrecio() { return precio; }
    public Integer getCantidad() { return cantidad; }
    public String getNotas() { return notas; }
    public Boolean getCompletado() { return completado; }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Integer productoId;
        private String nombre;
        private Double precio;
        private Integer cantidad;
        private String notas;
        private Boolean completado;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder productoId(Integer productoId) { this.productoId = productoId; return this; }
        public Builder nombre(String nombre) { this.nombre = nombre; return this; }
        public Builder precio(Double precio) { this.precio = precio; return this; }
        public Builder cantidad(Integer cantidad) { this.cantidad = cantidad; return this; }
        public Builder notas(String notas) { this.notas = notas; return this; }
        public Builder completado(Boolean completado) { this.completado = completado; return this; }

        public PedidoItemResponseDTO build() {
            return new PedidoItemResponseDTO(this);
        }
    }
}