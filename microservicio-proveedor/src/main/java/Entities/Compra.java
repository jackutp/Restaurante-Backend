package Entities;

import java.sql.Date;

public class Compra {
    int compraid;
    int proveedorid;
    int comprobanteCompraid;
    float monto;
    Date fechaCompra;
    public enum estadoCompra{
        RECIBIDO, PENDIENTE, CANCELADO, DEVUELTO;
    }

    public Compra(int compraid, int comprobanteCompraid, Date fechaCompra, float monto, int proveedorid) {
        this.compraid = compraid;
        this.comprobanteCompraid = comprobanteCompraid;
        this.fechaCompra = fechaCompra;
        this.monto = monto;
        this.proveedorid = proveedorid;
    }

    public int getCompraid() {
        return compraid;
    }

    public void setCompraid(int compraid) {
        this.compraid = compraid;
    }

    public int getComprobanteCompraid() {
        return comprobanteCompraid;
    }

    public void setComprobanteCompraid(int comprobanteCompraid) {
        this.comprobanteCompraid = comprobanteCompraid;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public int getProveedorid() {
        return proveedorid;
    }

    public void setProveedorid(int proveedorid) {
        this.proveedorid = proveedorid;
    }
}
