package com.Restaurante.reservas.service;

import com.Restaurante.reservas.dto.MesaRespuestaDto;
import com.Restaurante.reservas.dto.ReservaRespuestaDTO;

import java.util.List;

public interface ReservaReadService {
    List<ReservaRespuestaDTO> findAll();
    MesaRespuestaDto findMesaById(Long mesaId);
    List<MesaRespuestaDto> findAllMesas();
    ReservaRespuestaDTO findReservaById(Long id);
}
