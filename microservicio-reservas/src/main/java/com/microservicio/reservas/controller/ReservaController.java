package com.microservicio.reservas.controller;

import com.microservicio.reservas.dto.CrearReservaRequestDTO;
import com.microservicio.reservas.dto.ReservaResponseDTO;
import com.microservicio.reservas.entity.EstadoReserva;
import com.microservicio.reservas.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crearReserva(@Valid @RequestBody CrearReservaRequestDTO request) {
        ReservaResponseDTO reserva = reservaService.crearReserva(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorEstado(@PathVariable EstadoReserva estado) {
        return ResponseEntity.ok(reservaService.listarPorEstado(estado));
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorFecha(@PathVariable LocalDate fecha) {
        return ResponseEntity.ok(reservaService.listarPorFecha(fecha));
    }

    @GetMapping("/dia")
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasDelDia() {
        return ResponseEntity.ok(reservaService.listarReservasDelDia());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(reservaService.obtenerPorCodigo(codigo));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ReservaResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoReserva estado) {
        return ResponseEntity.ok(reservaService.actualizarEstado(id, estado));
    }
}