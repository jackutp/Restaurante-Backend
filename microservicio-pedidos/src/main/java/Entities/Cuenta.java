package Entities;

public class Cuenta {
    private int cuentaid;
    private boolean estado;
    private float total;
    private int mesaid;

    public Cuenta(int cuentaid, boolean estado, int mesaid, float total) {
        this.cuentaid = cuentaid;
        this.estado = estado;
        this.mesaid = mesaid;
        this.total = total;
    }

    public int getCuentaid() {
        return cuentaid;
    }

    public void setCuentaid(int cuentaid) {
        this.cuentaid = cuentaid;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getMesaid() {
        return mesaid;
    }

    public void setMesaid(int mesaid) {
        this.mesaid = mesaid;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
