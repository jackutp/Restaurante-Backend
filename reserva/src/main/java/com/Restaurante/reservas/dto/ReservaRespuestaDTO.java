package com.Restaurante.reservas.dto;

import com.Restaurante.reservas.entities.Reserva;

import java.sql.Date;
import java.sql.Time;

public record ReservaRespuestaDTO(Long reservaId, MesaRespuestaDto mesa, int clienteId, int cantidadClientes, Date fecha, Time hora, Reserva.menuTipo menu, String detalles) {
}
