package com.microservicio.inventario.Entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ingrediente")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingrediente_id")
    private Long ingredienteId;

    @Column(name = "nombre", length = 30, nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad", nullable = false)
    private UnidadMedida unidad;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoIngrediente estado;

    @OneToMany(mappedBy = "ingrediente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoIngrediente> productos = new ArrayList<>();

    @OneToMany(mappedBy = "ingrediente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MermaInsumos> mermas = new ArrayList<>();

    // Helper methods
    public void addProducto(ProductoIngrediente productoIngrediente) {
        productos.add(productoIngrediente);
        productoIngrediente.setIngrediente(this);
    }

    public void addMerma(MermaInsumos merma) {
        mermas.add(merma);
        merma.setIngrediente(this);
    }

    public void reducirStock(Integer cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
        } else {
            throw new RuntimeException("Stock insuficiente para el ingrediente: " + this.nombre);
        }
        if (this.stock == 0) {
            this.estado = EstadoIngrediente.VACIO;
        }
    }

    public void aumentarStock(Integer cantidad) {
        this.stock += cantidad;
        if (this.estado == EstadoIngrediente.VACIO && this.stock > 0) {
            this.estado = EstadoIngrediente.DISPONIBLE;
        }
    }


    public EstadoIngrediente getEstado() {
        return estado;
    }

    public void setEstado(EstadoIngrediente estado) {
        this.estado = estado;
    }

    public Long getIngredienteId() {
        return ingredienteId;
    }

    public void setIngredienteId(Long ingredienteId) {
        this.ingredienteId = ingredienteId;
    }

    public List<MermaInsumos> getMermas() {
        return mermas;
    }

    public void setMermas(List<MermaInsumos> mermas) {
        this.mermas = mermas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<ProductoIngrediente> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoIngrediente> productos) {
        this.productos = productos;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public UnidadMedida getUnidad() {
        return unidad;
    }

    public void setUnidad(UnidadMedida unidad) {
        this.unidad = unidad;
    }
}