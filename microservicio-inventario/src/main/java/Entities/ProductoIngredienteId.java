package Entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductoIngredienteId implements Serializable {
    private Long productoId;
    private Long ingredienteId;

    public ProductoIngredienteId() {}
    public ProductoIngredienteId(Long productoId, Long ingredienteId) {
        this.productoId = productoId;
        this.ingredienteId = ingredienteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoIngredienteId)) return false;
        ProductoIngredienteId that = (ProductoIngredienteId) o;
        return Objects.equals(productoId, that.productoId) &&
                Objects.equals(ingredienteId, that.ingredienteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productoId, ingredienteId);
    }

    // Getters y Setters
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Long getIngredienteId() { return ingredienteId; }
    public void setIngredienteId(Long ingredienteId) { this.ingredienteId = ingredienteId; }
}