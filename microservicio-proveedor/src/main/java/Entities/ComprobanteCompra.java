package Entities;

public class ComprobanteCompra {
    int comprobanteCompraid;
    int compraid;
    String archivoURL;
    public enum moneda{
        PEN, USD;
    }
    float igv;
    float subtotal;
    float total;

    public ComprobanteCompra(String archivoURL, int compraid, int comprobanteCompraid, float igv, float subtotal, float total) {
        this.archivoURL = archivoURL;
        this.compraid = compraid;
        this.comprobanteCompraid = comprobanteCompraid;
        this.igv = igv;
        this.subtotal = subtotal;
        this.total = total;
    }

    public String getArchivoURL() {
        return archivoURL;
    }

    public void setArchivoURL(String archivoURL) {
        this.archivoURL = archivoURL;
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

    public float getIgv() {
        return igv;
    }

    public void setIgv(float igv) {
        this.igv = igv;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
