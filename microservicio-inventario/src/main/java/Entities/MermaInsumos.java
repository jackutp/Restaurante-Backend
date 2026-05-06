package Entities;

import java.sql.Date;

public class MermaInsumos {
    int mermaInsumoid;
    int ingrediente;
    String motivo;
    Date fecha;

    public MermaInsumos(Date fecha, int ingrediente, int mermaInsumoid, String motivo) {
        this.fecha = fecha;
        this.ingrediente = ingrediente;
        this.mermaInsumoid = mermaInsumoid;
        this.motivo = motivo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(int ingrediente) {
        this.ingrediente = ingrediente;
    }

    public int getMermaInsumoid() {
        return mermaInsumoid;
    }

    public void setMermaInsumoid(int mermaInsumoid) {
        this.mermaInsumoid = mermaInsumoid;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
