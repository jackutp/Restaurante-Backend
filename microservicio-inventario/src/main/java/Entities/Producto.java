package Entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "nombre", length = 30, nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio", precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaProducto categoria;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoIngrediente> ingredientes = new ArrayList<>();

    // Helper methods
    public void addIngrediente(ProductoIngrediente productoIngrediente) {
        ingredientes.add(productoIngrediente);
        productoIngrediente.setProducto(this);
    }

    public void removeIngrediente(ProductoIngrediente productoIngrediente) {
        ingredientes.remove(productoIngrediente);
        productoIngrediente.setProducto(null);
    }
}