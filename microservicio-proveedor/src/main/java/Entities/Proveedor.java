package Entities;

public class Proveedor {
    int porveedorid;
    String nombreProveedor;
    String descripcion;
    String RUC;
    String razonSocial;
    String direccionFiscal;

    public Proveedor(String descripcion, String direccionFiscal, String nombreProveedor, int porveedorid, String razonSocial, String RUC) {
        this.descripcion = descripcion;
        this.direccionFiscal = direccionFiscal;
        this.nombreProveedor = nombreProveedor;
        this.porveedorid = porveedorid;
        this.razonSocial = razonSocial;
        this.RUC = RUC;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccionFiscal() {
        return direccionFiscal;
    }

    public void setDireccionFiscal(String direccionFiscal) {
        this.direccionFiscal = direccionFiscal;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public int getPorveedorid() {
        return porveedorid;
    }

    public void setPorveedorid(int porveedorid) {
        this.porveedorid = porveedorid;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRUC() {
        return RUC;
    }

    public void setRUC(String RUC) {
        this.RUC = RUC;
    }
}
