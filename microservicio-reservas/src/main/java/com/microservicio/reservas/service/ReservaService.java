package com.microservicio.reservas.service;

import com.microservicio.reservas.dto.CrearReservaRequestDTO;
import com.microservicio.reservas.dto.ReservaResponseDTO;
import com.microservicio.reservas.entity.EstadoReserva;
import com.microservicio.reservas.entity.Reserva;
import com.microservicio.reservas.exception.ResourceNotFoundException;
import com.microservicio.reservas.mapper.ReservaMapper;
import com.microservicio.reservas.repository.ReservaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private ReservaMapper reservaMapper;
    @Autowired
    private TokenService tokenService;

    @Transactional
    public ReservaResponseDTO crearReserva(CrearReservaRequestDTO request) {
        log.info("Creando reserva para {} el {} a las {} para {} personas", request.nombre(), request.fecha(), request.hora(), request.personas());
        Reserva reserva = reservaMapper.toEntity(request);
        reserva.setCodigo(tokenService.generarCodigoReserva());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        Reserva saved = reservaRepository.save(reserva);
        log.info("Reserva {} creada correctamente con código {}", saved.getId(), saved.getCodigo());
        return reservaMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.findAll().stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorEstado(EstadoReserva estado) {
        return reservaRepository.findByEstado(estado).stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorFecha(LocalDate fecha) {
        return reservaRepository.findByFecha(fecha).stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarReservasDelDia() {
        return reservaRepository.findByFecha(LocalDate.now()).stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));
        return reservaMapper.toResponseDTO(reserva);
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerPorCodigo(String codigo) {
        Reserva reserva = reservaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con código: " + codigo));
        return reservaMapper.toResponseDTO(reserva);
    }

    @Transactional
    public ReservaResponseDTO actualizarEstado(Long id, EstadoReserva nuevoEstado) {
        log.info("Actualizando estado de la reserva {} a {}", id, nuevoEstado);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        reserva.setEstado(nuevoEstado);
        reserva.setUpdatedAt(java.time.LocalDateTime.now());

        Reserva saved = reservaRepository.save(reserva);
        return reservaMapper.toResponseDTO(saved);
    }
}