package com.microservicio.Insumos.Entities;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "insumos")
@NoArgsConstructor
@AllArgsConstructor
public class Insumos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer insumoid;
    @Column(nullable = false, length = 100, unique = true)
    private String nombre;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadMedida unidadMedida;
    @Column(nullable = false)
    private Integer stock = 0;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInsumo estadoInsumo = EstadoInsumo.VACIO;
    // Método para actualizar el estado según el stock
    public void actualizarEstado() {
        if (stock <= 0) {
            this.estadoInsumo = EstadoInsumo.VACIO;
        } else if (stock < 10) {
            this.estadoInsumo = EstadoInsumo.BAJO;
        } else {
            this.estadoInsumo = EstadoInsumo.DISPONIBLE;
        }
    }
}