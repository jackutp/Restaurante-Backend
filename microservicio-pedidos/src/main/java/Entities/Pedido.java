package Entities;

public class Pedido {
    private int pedidoid;
    private String cuentaid;
    private float suma;

    public Pedido(String cuentaid, int pedidoid, float suma) {
        this.cuentaid = cuentaid;
        this.pedidoid = pedidoid;
        this.suma = suma;
    }


    public String getCuentaid() {
        return cuentaid;
    }

    public void setCuentaid(String cuentaid) {
        this.cuentaid = cuentaid;
    }

    public int getPedidoid() {
        return pedidoid;
    }

    public void setPedidoid(int pedidoid) {
        this.pedidoid = pedidoid;
    }

    public float getSuma() {
        return suma;
    }

    public void setSuma(float suma) {
        this.suma = suma;
    }
}
