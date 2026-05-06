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
}
