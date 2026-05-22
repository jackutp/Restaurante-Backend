package com.microservicio.Proveedor.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.LocalDateTime;
@Setter
@Getter
@Entity
@Table(name = "proveedores")
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer proveedorid;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(length = 500)
    private String descripcion;
    @Column(unique = true, length = 11)
    @Pattern(regexp = "^[0-9]{11}$")
    private String ruc;
    @Column(name = "razon_social", length = 200)
    private String razonSocial;
    @Column(name = "direccion_fiscal", length = 200)
    private String direccionFiscal;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}