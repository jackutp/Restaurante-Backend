package com.Restaurante.reservas.dto;

import com.Restaurante.reservas.entities.Reserva;

import java.sql.Date;
import java.sql.Time;
import java.util.Optional;

public record ReservaRequestDTO(Long mesaFk,int clienteId, int cantidadClientes, Date fecha, Time hora, Reserva.menuTipo menu, String detalles) {
}
