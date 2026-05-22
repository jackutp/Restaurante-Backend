package com.microservicio.pedidos.dto;

public class ActualizarTotalMesaRequestDTO {
    private Double total;

    public ActualizarTotalMesaRequestDTO() {}

    public ActualizarTotalMesaRequestDTO(Double total) {
        this.total = total;
    }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}