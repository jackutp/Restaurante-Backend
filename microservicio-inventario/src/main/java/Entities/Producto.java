package Entities;

public class Producto {
    private int productiid;
    private String nombreProducto;
    private String descripcion;
    private float precio;
    public enum categoria{
        PLATO, BEBIDA, POSTRE;
    }

    public Producto(String descripcion, String nombreProducto, float precio, int productiid) {
        this.descripcion = descripcion;
        this.nombreProducto = nombreProducto;
        this.precio = precio;
        this.productiid = productiid;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getProductiid() {
        return productiid;
    }

    public void setProductiid(int productiid) {
        this.productiid = productiid;
    }
}
