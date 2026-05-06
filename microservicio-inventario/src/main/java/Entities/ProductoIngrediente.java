package Entities;

public class ProductoIngrediente {
    int productoid;
    int ingredienteid;
    int cantidad;

    public ProductoIngrediente(int cantidad, int ingredienteid, int productoid) {
        this.cantidad = cantidad;
        this.ingredienteid = ingredienteid;
        this.productoid = productoid;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getIngredienteid() {
        return ingredienteid;
    }

    public void setIngredienteid(int ingredienteid) {
        this.ingredienteid = ingredienteid;
    }

    public int getProductoid() {
        return productoid;
    }

    public void setProductoid(int productoid) {
        this.productoid = productoid;
    }
}
