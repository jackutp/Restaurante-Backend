package com.Restaurante.reservas.service;

import com.Restaurante.reservas.dto.MesaDto;
import com.Restaurante.reservas.dto.ReservaRequestDTO;
import com.Restaurante.reservas.dto.ReservaRespuestaDTO;
import com.Restaurante.reservas.entities.Mesa;
import com.Restaurante.reservas.entities.Reserva;
import com.Restaurante.reservas.persistence.MesaRepository;
import com.Restaurante.reservas.persistence.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImp implements ReservaService{
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private MesaRepository mesaRepository;

    private MesaDto createMesaDTO(Mesa mesa){
       return new MesaDto(mesa.getMesaId(), mesa.getNumero(), mesa.getCapacidad(), mesa.isOcupado(), mesa.getTipo());
    }
    private ReservaRespuestaDTO createReservaRespuestaDto(Reserva r){
         return new ReservaRespuestaDTO(r.getReservaId(),createMesaDTO(r.getMesa()),r.getClienteId(), r.getCantidadClientes(), r.getFecha(), r.getHora(), r.getMenu(), r.getDetalles());
    }
    @Override
    public List<ReservaRespuestaDTO> findAll() {
         List<Reserva> reservasEncontradas = (List<Reserva>) reservaRepository.findAll();
         List<ReservaRespuestaDTO> reservasRespuesta = new ArrayList<>();
         for(Reserva r : reservasEncontradas){
             reservasRespuesta.add(createReservaRespuestaDto(r));
         }
         return reservasRespuesta;
    }
    @Override
    public MesaDto findMesaById(Long id) {
        Mesa mesa = mesaRepository.findById(id).orElseThrow();
        return createMesaDTO(mesa);
    }

    @Override
    public List<MesaDto> findAllMesas() {
        List<Mesa> mesas = (List<Mesa>) mesaRepository.findAll();
        List<MesaDto> mesasRespuesta = new ArrayList<>();
        for(Mesa m : mesas){
            mesasRespuesta.add(createMesaDTO(m));
        }
        return mesasRespuesta;
    }

    @Override
    public ReservaRespuestaDTO findReservaById(Long id) {
        Reserva r = reservaRepository.findById(id).orElseThrow();
        return createReservaRespuestaDto(r);
    }

    @Override
    public ReservaRespuestaDTO save(ReservaRequestDTO rdto) {
        Mesa mesa = mesaRepository.findById(rdto.mesaFk()).orElseThrow(() -> new RuntimeException("Mesa not found"));
        Reserva reserva = new Reserva();
        reserva.setMesa(mesa);
        reserva.setClienteId(rdto.clienteId());
        reserva.setCantidadClientes(rdto.cantidadClientes());
        reserva.setFecha(rdto.fecha());
        reserva.setHora(rdto.hora());
        reserva.setMenu(rdto.menu());
        reserva.setDetalles(rdto.detalles());
        reservaRepository.save(reserva);
        return createReservaRespuestaDto(reserva);
    }

    @Override
    public ReservaRespuestaDTO updateReserva(Long id, ReservaRequestDTO r) {
       Reserva updatedReserva = reservaRepository.findById(id).orElseThrow(() -> new RuntimeException("Reserva was not found"));
       Mesa mesa = mesaRepository.findById(r.mesaFk()).orElseThrow(() -> new RuntimeException("Mesa not found"));
       updatedReserva.setMesa(mesa);
        updatedReserva.setClienteId(r.clienteId());
        updatedReserva.setCantidadClientes(r.cantidadClientes());
        updatedReserva.setFecha(r.fecha());
        updatedReserva.setHora(r.hora());
        updatedReserva.setMenu(r.menu());
        updatedReserva.setDetalles(r.detalles());
        reservaRepository.save(updatedReserva);
        return createReservaRespuestaDto(updatedReserva);
    }

    @Override
    public boolean deleteReservation(Long id) {
        if(reservaRepository.existsById(id)){
            reservaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
