package com.microservicio.pedidos.Entities;
public class Cuenta {
    private Long cuentaid;
    private Long mesaid;
    private boolean estado;
    private float total;
    public Cuenta(Long cuentaid, boolean estado, Long mesaid, float total) {
        this.cuentaid = cuentaid;
        this.estado = estado;
        this.mesaid = mesaid;
        this.total = total;
    }
    public Long getCuentaid() {return cuentaid;}
    public void setCuentaid(Long cuentaid) {this.cuentaid = cuentaid;}
    public boolean isEstado() {return estado;}
    public void setEstado(boolean estado) {this.estado = estado;}
    public Long getMesaid() {return mesaid;}
    public void setMesaid(Long mesaid) {this.mesaid = mesaid;}
    public float getTotal() {return total;}
    public void setTotal(float total) {this.total = total;}
}
