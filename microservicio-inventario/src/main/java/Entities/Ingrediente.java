package Entities;

public class Ingrediente {
    int ingredienteid;
    String nombreIng;

    public enum unidad {
            KG, LATAS, G, L, ML;

    }

    int stock;
    public enum estado{
        DISPONIBLE, BAJO, VACIO;
    }

    public Ingrediente(int ingredienteid, String nombreIng, int stock) {
        this.ingredienteid = ingredienteid;
        this.nombreIng = nombreIng;
        this.stock = stock;
    }

    public int getIngredienteid() {
        return ingredienteid;
    }

    public void setIngredienteid(int ingredienteid) {
        this.ingredienteid = ingredienteid;
    }

    public String getNombreIng() {
        return nombreIng;
    }

    public void setNombreIng(String nombreIng) {
        this.nombreIng = nombreIng;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
