// controller/MesaController.java
package com.microservicio.mesas.controller;

import com.microservicio.mesas.dto.*;
import com.microservicio.mesas.entity.EstadoMesa;
import com.microservicio.mesas.service.MesaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    public ResponseEntity<List<MesaResponseDTO>> getAllMesas() {
        return ResponseEntity.ok(mesaService.getAllMesas());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<MesaResponseDTO>> getMesasByEstado(@PathVariable EstadoMesa estado) {
        return ResponseEntity.ok(mesaService.getMesasByEstado(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> getMesaById(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.getMesaById(id));
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<MesaResponseDTO> getMesaByNumero(@PathVariable Integer numero) {
        return ResponseEntity.ok(mesaService.getMesaByNumero(numero));
    }

    @PostMapping
    public ResponseEntity<MesaResponseDTO> createMesa(@Valid @RequestBody CrearMesaRequestDTO request) {
        MesaResponseDTO newMesa = mesaService.createMesa(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMesa);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<MesaResponseDTO> updateEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoMesaRequestDTO request) {
        return ResponseEntity.ok(mesaService.updateEstado(id, request));
    }

    @PatchMapping("/{id}/total")
    public ResponseEntity<MesaResponseDTO> updateTotal(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarTotalMesaRequestDTO request) {
        return ResponseEntity.ok(mesaService.updateTotal(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMesa(@PathVariable Long id) {
        mesaService.deleteMesa(id);
        return ResponseEntity.noContent().build();
    }
}