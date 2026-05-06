package com.Restaurante.reservas.service;

import com.Restaurante.reservas.dto.MesaDto;
import com.Restaurante.reservas.dto.ReservaRequestDTO;
import com.Restaurante.reservas.dto.ReservaRespuestaDTO;
import com.Restaurante.reservas.entities.Mesa;
import com.Restaurante.reservas.entities.Reserva;

import java.util.List;

public interface ReservaService {
    List<ReservaRespuestaDTO> findAll();
    MesaDto findMesaById(Long mesaId);
    List<MesaDto> findAllMesas();
    ReservaRespuestaDTO findReservaById(Long id);
    ReservaRespuestaDTO save(ReservaRequestDTO rdto);
    ReservaRespuestaDTO updateReserva(Long id, ReservaRequestDTO r);
    boolean deleteReservation(Long id);
}
