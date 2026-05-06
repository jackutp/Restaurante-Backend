package Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productoingrediente")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoIngrediente {

    @Id
    @ManyToOne
    @JoinColumn(name = "producto_fk", nullable = false)
    private Producto producto;

    @Id
    @ManyToOne
    @JoinColumn(name = "ingrediente_fk", nullable = false)
    private Ingrediente ingrediente;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
}