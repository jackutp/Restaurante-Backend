package com.microservicio.pedidos.Entities;

public class Pedido {
    private Long pedidoid;
    private Long cuentaid;
    private float suma;

    public Pedido(Long cuentaid, Long pedidoid, float suma) {
        this.cuentaid = cuentaid;
        this.pedidoid = pedidoid;
        this.suma = suma;
    }
    public Long getCuentaid() {return cuentaid;}
    public void setCuentaid(Long cuentaid) {this.cuentaid = cuentaid;}
    public Long getPedidoid() {return pedidoid;}
    public void setPedidoid(Long pedidoid) {this.pedidoid = pedidoid;}
    public float getSuma() {return suma;}
    public void setSuma(float suma) {this.suma = suma;}
}
