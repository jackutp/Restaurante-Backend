// service/MesaService.java
package com.microservicio.mesas.service;

import com.microservicio.mesas.dto.*;
import com.microservicio.mesas.entity.EstadoMesa;
import com.microservicio.mesas.entity.Mesa;
import com.microservicio.mesas.mapper.MesaMapper;
import com.microservicio.mesas.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;
    private final MesaMapper mesaMapper;

    // Obtener todas las mesas
    public List<MesaResponseDTO> getAllMesas() {
        return mesaRepository.findAll().stream()
                .map(mesaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener mesas por estado
    public List<MesaResponseDTO> getMesasByEstado(EstadoMesa estado) {
        return mesaRepository.findByEstado(estado).stream()
                .map(mesaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener una mesa por ID
    public MesaResponseDTO getMesaById(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + id));
        return mesaMapper.toResponseDTO(mesa);
    }

    // Obtener mesa por número
    public MesaResponseDTO getMesaByNumero(Integer numero) {
        Mesa mesa = mesaRepository.findByNumero(numero)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con número: " + numero));
        return mesaMapper.toResponseDTO(mesa);
    }

    // Crear nueva mesa
    @Transactional
    public MesaResponseDTO createMesa(CrearMesaRequestDTO request) {
        if (mesaRepository.existsByNumero(request.getNumero())) {
            throw new RuntimeException("Ya existe una mesa con el número: " + request.getNumero());
        }

        Mesa mesa = new Mesa(
                request.getNumero(),
                request.getCapacidad(),
                EstadoMesa.DISPONIBLE
        );

        Mesa savedMesa = mesaRepository.save(mesa);
        return mesaMapper.toResponseDTO(savedMesa);
    }

    // Actualizar estado de la mesa
    @Transactional
    public MesaResponseDTO updateEstado(Long id, ActualizarEstadoMesaRequestDTO request) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + id));

        mesa.setEstado(request.getEstado());
        mesa.setUpdatedAt(LocalDateTime.now());

        // Si se ocupa y viene total, actualizar
        if (request.getEstado() == EstadoMesa.OCUPADO && request.getTotalActual() != null) {
            mesa.setTotalActual(request.getTotalActual());
            mesa.setOrdenActualId(request.getOrdenActualId());
        }

        // Si se libera, resetear total y orden
        if (request.getEstado() == EstadoMesa.DISPONIBLE) {
            mesa.setTotalActual(0.0);
            mesa.setOrdenActualId(null);
        }

        Mesa updatedMesa = mesaRepository.save(mesa);
        return mesaMapper.toResponseDTO(updatedMesa);
    }

    // Actualizar total de la mesa
    @Transactional
    public MesaResponseDTO updateTotal(Long id, ActualizarTotalMesaRequestDTO request) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + id));

        mesa.setTotalActual(request.getTotal());
        mesa.setUpdatedAt(LocalDateTime.now());

        Mesa updatedMesa = mesaRepository.save(mesa);
        return mesaMapper.toResponseDTO(updatedMesa);
    }

    // Eliminar mesa (solo si está disponible)
    @Transactional
    public void deleteMesa(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + id));

        if (mesa.getEstado() != EstadoMesa.DISPONIBLE) {
            throw new RuntimeException("No se puede eliminar una mesa ocupada o reservada");
        }

        mesaRepository.delete(mesa);
    }
}