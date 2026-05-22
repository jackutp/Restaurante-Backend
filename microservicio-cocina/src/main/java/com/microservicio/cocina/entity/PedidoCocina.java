package com.microservicio.cocina.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "pedidos_cocina")
public class PedidoCocina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "orden_id", unique = true, nullable = false)
    private String ordenId;
    @Column(name = "mesa_numero", nullable = false)
    private Integer mesaNumero;
    @Column(name = "hora")
    private String hora;
    @Column(name = "estado")
    private String estado = "PENDIENTE";
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemCocina> items = new ArrayList<>();
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    public PedidoCocina() {}
    public PedidoCocina(String ordenId, Integer mesaNumero, String hora) {
        this.ordenId = ordenId;
        this.mesaNumero = mesaNumero;
        this.hora = hora;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }
    public Integer getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(Integer mesaNumero) { this.mesaNumero = mesaNumero; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<ItemCocina> getItems() { return items; }
    public void setItems(List<ItemCocina> items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}