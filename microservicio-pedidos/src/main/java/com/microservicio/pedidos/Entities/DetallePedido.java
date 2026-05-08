package com.microservicio.pedidos.Entities;
public class DetallePedido {
    private Long detallePedidoid;
    private Long pedidoid;
    private Long productoid;
    private int cantidad;
    private String nota;
    private Estado estado;

    public DetallePedido(int cantidad, Long detallePedidoid, Estado estado, String nota, Long pedidoid, Long productoid) {
        this.cantidad = cantidad;
        this.detallePedidoid = detallePedidoid;
        this.estado = estado;
        this.nota = nota;
        this.pedidoid = pedidoid;
        this.productoid = productoid;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public Long getDetallePedidoid() {
        return detallePedidoid;
    }
    public void setDetallePedidoid(Long detallePedidoid) {
        this.detallePedidoid = detallePedidoid;
    }
    public Estado getEstado() {
        return estado;
    }
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    public String getNota() {
        return nota;
    }
    public void setNota(String nota) {
        this.nota = nota;
    }
    public Long getPedidoid() {
        return pedidoid;
    }
    public void setPedidoid(Long pedidoid) {
        this.pedidoid = pedidoid;
    }
    public Long getProductoid() {
        return productoid;
    }
    public void setProductoid(Long productoid) {
        this.productoid = productoid;
    }
}
