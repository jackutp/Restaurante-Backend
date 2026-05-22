package com.microservicio.pedidos.dto;
public class ActualizarEstadoMesaRequestDTO {
    private String estado;
    private Double totalActual;
    private String ordenActualId;
    public ActualizarEstadoMesaRequestDTO() {}
    public ActualizarEstadoMesaRequestDTO(String estado, Double totalActual, String ordenActualId) {
        this.estado = estado;
        this.totalActual = totalActual;
        this.ordenActualId = ordenActualId;
    }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getTotalActual() { return totalActual; }
    public void setTotalActual(Double totalActual) { this.totalActual = totalActual; }
    public String getOrdenActualId() { return ordenActualId; }
    public void setOrdenActualId(String ordenActualId) { this.ordenActualId = ordenActualId; }
}