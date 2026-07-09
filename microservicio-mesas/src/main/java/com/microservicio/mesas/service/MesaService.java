// service/MesaService.java
package com.microservicio.mesas.service;

import com.microservicio.mesas.dto.*;
import com.microservicio.mesas.entity.EstadoMesa;
import com.microservicio.mesas.entity.Mesa;
import com.microservicio.mesas.exception.ConflictException;
import com.microservicio.mesas.exception.ResourceNotFoundException;
import com.microservicio.mesas.mapper.MesaMapper;
import com.microservicio.mesas.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MesaService {
    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private MesaMapper mesaMapper;

    // Obtener todas las mesas
    @Transactional(readOnly = true)
    public List<MesaResponseDTO> getAllMesas() {
        return mesaRepository.findAll().stream()
                .map(mesaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener mesas por estado
    @Transactional(readOnly = true)
    public List<MesaResponseDTO> getMesasByEstado(EstadoMesa estado) {
        return mesaRepository.findByEstado(estado).stream()
                .map(mesaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener una mesa por ID
    @Transactional(readOnly = true)
    public MesaResponseDTO getMesaById(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));
        return mesaMapper.toResponseDTO(mesa);
    }

    // Obtener mesa por número
    @Transactional(readOnly = true)
    public MesaResponseDTO getMesaByNumero(Integer numero) {
        Mesa mesa = mesaRepository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con número: " + numero));
        return mesaMapper.toResponseDTO(mesa);
    }

    // Crear nueva mesa
    @Transactional
    public MesaResponseDTO createMesa(CrearMesaRequestDTO request) {
        log.info("Registrando mesa {} con la capacidad {}", request.getNumero(), request.getCapacidad());
        if (mesaRepository.existsByNumero(request.getNumero())) {
            log.warn("Se intentó registrar una mesa con un número ya existente {}", request.getNumero());
            throw new ConflictException("Ya existe una mesa con el número: " + request.getNumero());
        }
        Mesa mesa = new Mesa(
                request.getNumero(),
                request.getCapacidad(),
                EstadoMesa.DISPONIBLE
        );
        Mesa savedMesa = mesaRepository.save(mesa);
        log.info("Mesa {} registrada correctamente (id={})", savedMesa.getNumero(), savedMesa.getId());
        return mesaMapper.toResponseDTO(savedMesa);
    }

    // Actualizar estado de la mesa
    @Transactional
    public MesaResponseDTO updateEstado(Long id, ActualizarEstadoMesaRequestDTO request) {
        log.info("Actualizando mesa con id {}", id);
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));

        mesa.setEstado(request.getEstado());
        mesa.setUpdatedAt(LocalDateTime.now());
        // Si se ocupa y viene total, actualizar
        if (request.getEstado() == EstadoMesa.OCUPADO && request.getTotalActual() != null) {
            mesa.setTotalActual(request.getTotalActual());
            mesa.setOrdenActualId(request.getOrdenActualId());
            log.info("Mesa {} ocupada. Orden={}, Total inicial={}", id, request.getOrdenActualId(), request.getTotalActual());
        }
        // Si se libera, resetear total y orden
        if (request.getEstado() == EstadoMesa.DISPONIBLE) {
            mesa.setTotalActual(0.0);
            mesa.setOrdenActualId(null);
            log.info("Mesa {} liberada", id);
        }
        Mesa updatedMesa = mesaRepository.save(mesa);
        log.info("Estado de la mesa {} actualizada correctamente", id);
        return mesaMapper.toResponseDTO(updatedMesa);
    }

    // Actualizar total de la mesa
    @Transactional
    public MesaResponseDTO updateTotal(Long id, ActualizarTotalMesaRequestDTO request) {
        log.info("Actualizando mesa con id {}", id);
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));
        double anterior = mesa.getTotalActual();
        mesa.setTotalActual(request.getTotal());
        mesa.setUpdatedAt(LocalDateTime.now());

        Mesa updatedMesa = mesaRepository.save(mesa);
        log.info("Mesa {} - Total actualizado de {} a {}", anterior, mesa.getTotalActual());
        return mesaMapper.toResponseDTO(updatedMesa);
    }

    @Transactional
    public void deleteMesa(Long id) {
        log.info("Eliminando mesa {}", id);
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));

        if (mesa.getEstado() != EstadoMesa.DISPONIBLE) {
            log.warn("Se intentó eliminar la mesa {} con estado {}", id, mesa.getEstado());
            throw new ConflictException("No se puede eliminar una mesa ocupada o reservada");
        }

        mesaRepository.delete(mesa);
    }

    // Actualizar mesa
    @Transactional
    public MesaResponseDTO updateMesa(Long id, CrearMesaRequestDTO request) {
        log.info("Actualizando mesa con id {}", id);
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con ID: " + id));
        if (mesa.getEstado() != EstadoMesa.DISPONIBLE) {
            log.warn("Se intentó editar la mesa {} mientras estaba {}", id, mesa.getEstado());
            throw new ConflictException("No se puede editar una mesa ocupada o reservada");
        }
        if (mesaRepository.existsByNumero(request.getNumero()) && !mesa.getNumero().equals(request.getNumero())) {
            log.warn("Se intentó registrar una mesa con un número ya existente {}", request.getNumero());
            throw new ConflictException("Ya existe una mesa con el número: " + request.getNumero());
        }
        mesa.setNumero(request.getNumero());
        mesa.setCapacidad(request.getCapacidad());
        mesa.setUpdatedAt(LocalDateTime.now());
        Mesa updatedMesa = mesaRepository.save(mesa);
        log.info("Mesa {} actualizada correctamente", id);
        return mesaMapper.toResponseDTO(updatedMesa);
    }

    // ========== BUSCAR POR NÚMERO DE MESA ==========
    // Actualizar estado por número de mesa
    @Transactional
    public MesaResponseDTO updateEstadoByNumero(Integer numero, ActualizarEstadoMesaRequestDTO request) {
        log.info("Actualizando mesa con numero {}", numero);
        Mesa mesa = mesaRepository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con número: " + numero));
        log.info("Total actual de mesa " + numero + ": " + mesa.getTotalActual());
        log.info("Total recibido: " + request.getTotalActual());
        mesa.setEstado(request.getEstado());
        mesa.setUpdatedAt(LocalDateTime.now());
        if (request.getEstado() == EstadoMesa.OCUPADO && request.getTotalActual() != null) {
            // SUMAR al total existente
            double nuevoTotal = mesa.getTotalActual() + request.getTotalActual();
            mesa.setTotalActual(nuevoTotal);
            mesa.setOrdenActualId(request.getOrdenActualId());
            log.info("Nuevo total después de sumar: " + nuevoTotal);
            log.info("Mesa {} - Total actualizado de {} a {}", numero, mesa.getTotalActual(), nuevoTotal);
        }

        if (request.getEstado() == EstadoMesa.DISPONIBLE) {
            mesa.setTotalActual(0.0);
            mesa.setOrdenActualId(null);
        }
        Mesa updatedMesa = mesaRepository.save(mesa);
        log.info("Mesa {} liberada", numero);
        return mesaMapper.toResponseDTO(updatedMesa);
    }

    // Actualizar total por número de mesa
    @Transactional
    public MesaResponseDTO updateTotalByNumero(Integer numero, ActualizarTotalMesaRequestDTO request) {
        log.info("Actualizando mesa con numero {}", numero);
        Mesa mesa = mesaRepository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con número: " + numero));
        double anterior = mesa.getTotalActual();
        // SUMAR al total existente
        double nuevoTotal = anterior + request.getTotal();
        mesa.setTotalActual(nuevoTotal);
        mesa.setUpdatedAt(LocalDateTime.now());
        log.info("Mesa " + numero + " - Total actualizado vía PATCH: " + nuevoTotal);
        Mesa updatedMesa = mesaRepository.save(mesa);
        log.info("Mesa {} - Total acumulado de {} a {}", anterior, nuevoTotal);
        return mesaMapper.toResponseDTO(updatedMesa);
    }
}