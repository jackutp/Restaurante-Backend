package com.microservicio.pagos.dto;

public class MesaResponseDTO {
    private Long id;
    private Integer numero;
    private Integer capacidad;
    private String estado;
    private Double totalActual;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getTotalActual() { return totalActual; }
    public void setTotalActual(Double totalActual) { this.totalActual = totalActual; }
}