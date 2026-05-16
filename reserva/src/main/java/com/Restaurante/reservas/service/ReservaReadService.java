package com.Restaurante.reservas.service;

import com.Restaurante.reservas.dto.MesaDto;
import com.Restaurante.reservas.dto.ReservaRequestDTO;
import com.Restaurante.reservas.dto.ReservaRespuestaDTO;

import java.util.List;

public interface ReservaReadService {
    List<ReservaRespuestaDTO> findAll();
    MesaDto findMesaById(Long mesaId);
    List<MesaDto> findAllMesas();
    ReservaRespuestaDTO findReservaById(Long id);
}
