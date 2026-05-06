package Entities;

public class DetallePedido {
    private int detallePedidoid;
    private int pedidoid;
    private int platilloid;
    private int cantidad;
    private String nota;
    public enum estado{
        EMITIDO, DESPACHAR, COMPLETADO, CANCELADO
    }

    public DetallePedido(int cantidad, int detallePedidoid, String nota, int pedidoid, int platilloid) {
        this.cantidad = cantidad;
        this.detallePedidoid = detallePedidoid;
        this.nota = nota;
        this.pedidoid = pedidoid;
        this.platilloid = platilloid;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getDetallePedidoid() {
        return detallePedidoid;
    }

    public void setDetallePedidoid(int detallePedidoid) {
        this.detallePedidoid = detallePedidoid;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public int getPedidoid() {
        return pedidoid;
    }

    public void setPedidoid(int pedidoid) {
        this.pedidoid = pedidoid;
    }

    public int getPlatilloid() {
        return platilloid;
    }

    public void setPlatilloid(int platilloid) {
        this.platilloid = platilloid;
    }
}
