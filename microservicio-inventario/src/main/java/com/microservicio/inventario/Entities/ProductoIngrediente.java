package com.microservicio.inventario.Entities;

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

    @EmbeddedId
    private ProductoIngredienteId id;

    // Reemplaza los dos @Id @ManyToOne anteriores por:
    @ManyToOne
    @MapsId("productoId")
    @JoinColumn(name = "producto_fk", nullable = false)
    private Producto producto;

    @ManyToOne
    @MapsId("ingredienteId")
    @JoinColumn(name = "ingrediente_fk", nullable = false)
    private Ingrediente ingrediente;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public ProductoIngredienteId getId() {
        return id;
    }

    public void setId(ProductoIngredienteId id) {
        this.id = id;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}

/*
package com.microservicio.inventario.Entities;

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

    @EmbeddedId
    private ProductoIngredienteId id;

    @ManyToOne
    @MapsId("productoId")
    @JoinColumn(name = "producto_fk", nullable = false)
    private Producto producto;

    @ManyToOne
    @MapsId("ingredienteId")
    @JoinColumn(name = "ingrediente_fk", nullable = false)
    private Ingrediente ingrediente;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public ProductoIngredienteId getId() {
        return id;
    }

    public void setId(ProductoIngredienteId id) {
        this.id = id;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}

 */