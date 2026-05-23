package com.microservicio.Proveedor.Entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Setter
@Getter
@Entity
@Table(name = "ordenes_compra")
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ordenId;
    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;
    @Column(nullable = false)
    private LocalDateTime fecha;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estado = EstadoOrden.PENDIENTE;
    @Column(name = "factura_nombre")
    private String facturaNombre;
    @Column(name = "factura_tipo")
    private String facturaTipo;
    @Column(name = "factura_contenido", columnDefinition = "BYTEA")
    private byte[] facturaContenido;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estado == null) estado = EstadoOrden.PENDIENTE;
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}