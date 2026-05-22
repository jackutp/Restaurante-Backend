package com.microservicio.Mermas.Controller;
import com.microservicio.Mermas.Entities.TipoMerma;
import com.microservicio.Mermas.Services.MermaService;
import com.microservicio.Mermas.dto.MermaDTO;
import com.microservicio.Mermas.dto.MermaRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/mermas")
public class MermaController {
    private final MermaService mermaService;
    public MermaController(MermaService mermaService) {
        this.mermaService = mermaService;
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(mermaService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        var merma = mermaService.findById(id);
        if (merma.isPresent()) {
            return ResponseEntity.ok(merma.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Merma no encontrada con id: " + id));
        }
    }
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> getByTipo(@PathVariable TipoMerma tipo) {
        return ResponseEntity.ok(mermaService.findByTipo(tipo));
    }
    @GetMapping("/productos")
    public ResponseEntity<?> getProductos() {
        try {
            return ResponseEntity.ok(mermaService.getProductos());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener productos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }
    @GetMapping("/insumos")
    public ResponseEntity<?> getInsumos() {
        try {
            return ResponseEntity.ok(mermaService.getInsumos());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener insumos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MermaRequestDTO mermaDTO) {
        try {
            MermaDTO saved = mermaService.save(mermaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear merma: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Valid @RequestBody MermaRequestDTO mermaDTO) {
        try {
            MermaDTO updated = mermaService.update(id, mermaDTO);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            mermaService.delete(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Merma eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}