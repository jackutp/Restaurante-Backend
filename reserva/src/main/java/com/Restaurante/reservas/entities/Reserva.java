package com.Restaurante.reservas.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@Builder
@Table(name="reservas")
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Reserva {
    public enum menuTipo {
        DEGUSTACION, CARTA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserva_id", nullable = false)
    private Long reservaId;
    @ManyToOne
    @JoinColumn(name = "mesa_fk", nullable = false)
    private Mesa mesa;
    @Column(name = "cliente_id", nullable = false)
    private int clienteId;
    @Column(name = "cantidad_clientes", nullable = false)
    private int cantidadClientes;
    @Column(nullable = false)
    private Date fecha;
    @Column(nullable = false)
    private Time hora;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private menuTipo menu;
    @Column(nullable = true)
    private String detalles;

    public Reserva(Long reservaId, Mesa mesa, int cantidadClientes, Date fecha, Time hora, menuTipo menu) {
        this.reservaId = reservaId;
        this.mesa = mesa;
        this.cantidadClientes = cantidadClientes;
        this.fecha = fecha;
        this.hora = hora;
        this.menu = menu;
    }
}
