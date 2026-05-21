package com.microservicio.pagos.dto;

public class ProcesarPagoResponseDTO {
    private Long pagoId;
    private String ordenId;
    private Integer mesaNumero;
    private Double total;
    private String metodo;
    private String estado;
    private ComprobanteResponseDTO comprobante;

    // Getters y Setters
    public Long getPagoId() { return pagoId; }
    public void setPagoId(Long pagoId) { this.pagoId = pagoId; }
    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }
    public Integer getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(Integer mesaNumero) { this.mesaNumero = mesaNumero; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public ComprobanteResponseDTO getComprobante() { return comprobante; }
    public void setComprobante(ComprobanteResponseDTO comprobante) { this.comprobante = comprobante; }
}