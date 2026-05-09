package com.microservicio.Mermas.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "mermas")
@NoArgsConstructor
@AllArgsConstructor
public class Merma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mermaid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMerma tipoMerma;

    @Column(nullable = false)
    private String nombreMerma;

    @Column(nullable = false)
    private String cantidad;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "referencia_id")
    private Integer referenciaId;  // ID del producto o insumo

    @Column(name = "unidad_medida")
    private String unidadMedida;  // Solo para insumos

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }
}