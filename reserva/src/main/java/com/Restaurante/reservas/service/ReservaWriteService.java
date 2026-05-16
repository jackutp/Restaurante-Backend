package com.Restaurante.reservas.service;

import com.Restaurante.reservas.dto.ReservaRequestDTO;
import com.Restaurante.reservas.dto.ReservaRespuestaDTO;

public interface ReservaWriteService {
    ReservaRespuestaDTO save(ReservaRequestDTO rdto);
    ReservaRespuestaDTO updateReserva(Long id, ReservaRequestDTO r);
    boolean deleteReservation(Long id);
}
