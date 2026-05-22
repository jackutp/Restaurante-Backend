package com.microservicio.Insumos.Controller;

import com.microservicio.Insumos.Entities.EstadoInsumo;
import com.microservicio.Insumos.Services.InsumoServiceRead;
import com.microservicio.Insumos.Services.InsumoServiceWrite;
import com.microservicio.Insumos.dto.InsumoDTO;
import com.microservicio.Insumos.dto.InsumoRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/insumos")
public class InsumoController {

    @Autowired
    private InsumoServiceRead insumoRead;
    @Autowired
    private InsumoServiceWrite insumoWrite;


    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(insumoRead.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        var insumo = insumoRead.findById(id);
        if (insumo.isPresent()) {
            return ResponseEntity.ok(insumo.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Insumo no encontrado con id: " + id));
        }
    }

    // NUEVO: Búsqueda por nombre
    @GetMapping("/search")
    public ResponseEntity<?> searchByNombre(@RequestParam String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El parámetro 'nombre' es requerido"));
        }
        var resultados = insumoRead.findByNombre(nombre);
        if (resultados.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No se encontraron insumos con: " + nombre));
        }
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getByEstado(@PathVariable EstadoInsumo estado) {
        return ResponseEntity.ok(insumoRead.findByEstado(estado));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStock() {
        return ResponseEntity.ok(insumoRead.findLowStock());
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<?> getOutOfStock() {
        return ResponseEntity.ok(insumoRead.findOutOfStock());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody InsumoRequestDTO insumoDTO) {
        try {
            InsumoDTO saved = insumoWrite.save(insumoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al crear insumo: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Valid @RequestBody InsumoRequestDTO insumoDTO) {
        try {
            InsumoDTO updated = insumoWrite.update(id, insumoDTO);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Integer id,
                                         @RequestBody Map<String, Integer> request) {
        try {
            Integer nuevoStock = request.get("stock");
            if (nuevoStock == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El campo 'stock' es requerido"));
            }
            InsumoDTO updated = insumoWrite.updateStock(id, nuevoStock);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            insumoWrite.delete(id);
            return ResponseEntity.ok(Map.of("message", "Insumo eliminado exitosamente"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}