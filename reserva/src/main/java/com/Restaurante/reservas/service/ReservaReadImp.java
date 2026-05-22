package com.Restaurante.reservas.service;

import com.Restaurante.reservas.dto.MesaRespuestaDto;
import com.Restaurante.reservas.dto.ReservaRespuestaDTO;
import com.Restaurante.reservas.entities.Mesa;
import com.Restaurante.reservas.entities.Reserva;
import com.Restaurante.reservas.persistence.MesaRepository;
import com.Restaurante.reservas.persistence.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReservaReadImp implements ReservaReadService{
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private MesaRepository mesaRepository;

    private MesaRespuestaDto createMesaDTO(Mesa mesa){
        return new MesaRespuestaDto(mesa.getMesaId(), mesa.getNumero(), mesa.getCapacidad(), mesa.isOcupado(), mesa.getTipo());
    }
    private ReservaRespuestaDTO createReservaRespuestaDto(Reserva r){
        return new ReservaRespuestaDTO(r.getReservaId(),createMesaDTO(r.getMesa()),r.getClienteId(), r.getCantidadClientes(), r.getFecha(), r.getHora(), r.getMenu(), r.getDetalles());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaRespuestaDTO> findAll() {
        List<Reserva> reservasEncontradas = (List<Reserva>) reservaRepository.findAll();
        List<ReservaRespuestaDTO> reservasRespuesta = new ArrayList<>();
        for(Reserva r : reservasEncontradas){
            reservasRespuesta.add(createReservaRespuestaDto(r));
        }
        return reservasRespuesta;
    }

    @Override
    @Transactional(readOnly = true)
    public MesaRespuestaDto findMesaById(Long mesaId) {
        Mesa mesa = mesaRepository.findById(mesaId).orElseThrow();
        return createMesaDTO(mesa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesaRespuestaDto> findAllMesas() {
        List<Mesa> mesas = (List<Mesa>) mesaRepository.findAll();
        List<MesaRespuestaDto> mesasRespuesta = new ArrayList<>();
        for(Mesa m : mesas){
            mesasRespuesta.add(createMesaDTO(m));
        }
        return mesasRespuesta;
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaRespuestaDTO findReservaById(Long id) {
        Reserva r = reservaRepository.findById(id).orElseThrow();
        return createReservaRespuestaDto(r);
    }
}
