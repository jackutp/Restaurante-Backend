package com.microservicio.cocina.dto;

public class ItemCocinaResponseDTO {
    private Long id;
    private Integer productoId;
    private String nombre;
    private Integer cantidad;
    private String notas;
    private Boolean completado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
}