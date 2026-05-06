package Entities;

public class DetalleCompra {
    int detalleCompraid;
    int compraid;
    int ingredienteid;
    int cantidad;
    float precioUnitario;

    public DetalleCompra(int cantidad, int compraid, int detalleCompraid, int ingredienteid, float precioUnitario) {
        this.cantidad = cantidad;
        this.compraid = compraid;
        this.detalleCompraid = detalleCompraid;
        this.ingredienteid = ingredienteid;
        this.precioUnitario = precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCompraid() {
        return compraid;
    }

    public void setCompraid(int compraid) {
        this.compraid = compraid;
    }

    public int getDetalleCompraid() {
        return detalleCompraid;
    }

    public void setDetalleCompraid(int detalleCompraid) {
        this.detalleCompraid = detalleCompraid;
    }

    public int getIngredienteid() {
        return ingredienteid;
    }

    public void setIngredienteid(int ingredienteid) {
        this.ingredienteid = ingredienteid;
    }

    public float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
