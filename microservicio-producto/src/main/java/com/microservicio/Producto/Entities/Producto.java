package com.microservicio.Producto.Entities;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
@Setter
@Getter
@Entity
@Table(name = "productos")
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productoid;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(length = 500)
    private String descripcion;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;
    @Column(name = "imagen_producto")
    private String imagenProducto;
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;  // 👈 NUEVO CAMPO
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (stock == null) stock = 0;
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}