package com.microservicio.cocina.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "items_cocina")
public class ItemCocina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "notas")
    private String notas;

    @Column(name = "completado")
    private Boolean completado = false;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoCocina pedido;

    public ItemCocina() {}

    public ItemCocina(Integer productoId, String nombre, Integer cantidad, String notas) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.notas = notas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
    public PedidoCocina getPedido() { return pedido; }
    public void setPedido(PedidoCocina pedido) { this.pedido = pedido; }
}