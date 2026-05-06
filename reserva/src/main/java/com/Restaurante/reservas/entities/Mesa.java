package com.Restaurante.reservas.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="mesa")
@Entity
public class Mesa {
    public enum mesa_tipo{
        NORMAL, VIP, TERRAZA, BARRA, PRIVADA
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mesa_id")
    private Long mesaId;
    @Column(nullable = false)
    private int numero;
    @Column(nullable = false)
    private int capacidad;
    @Column(nullable = false)
    private boolean ocupado;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private mesa_tipo tipo;
}
