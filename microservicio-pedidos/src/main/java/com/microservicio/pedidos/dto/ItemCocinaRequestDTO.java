package com.microservicio.pedidos.dto;

public class ItemCocinaRequestDTO {
    private Integer productoId;
    private String nombre;
    private Integer cantidad;
    private String notas;

    // Getters y Setters
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}