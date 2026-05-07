
package com.microservicio.inventario.Controller;

import com.microservicio.inventario.Services.IngredienteService;
import com.microservicio.inventario.dto.CrearIngredienteDTO;
import com.microservicio.inventario.dto.IngredienteDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingredientes")
public class IngredienteController {

    private final IngredienteService service;

    public IngredienteController(IngredienteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable Long id) {
        Optional<IngredienteDTO> opt = service.findById(id);
        if (opt.isPresent()) return ResponseEntity.ok(opt.get());
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CrearIngredienteDTO ingrediente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(ingrediente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CrearIngredienteDTO ingrediente) {
        Optional<IngredienteDTO> opt = service.update(id, ingrediente);
        if (opt.isPresent()) return ResponseEntity.ok(opt.get());
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = service.deleteById(id);
        if (deleted) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    // PATCH /api/ingredientes/{id}/reducir?cantidad=5
    @PatchMapping("/{id}/reducir")
    public ResponseEntity<?> reducirStock(@PathVariable Long id,
                                          @RequestParam Integer cantidad) {
        try {
            Optional<IngredienteDTO> opt = service.reducirStock(id, cantidad);
            if (opt.isPresent()) return ResponseEntity.ok(opt.get());
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PATCH /api/ingredientes/{id}/aumentar?cantidad=10
    @PatchMapping("/{id}/aumentar")
    public ResponseEntity<?> aumentarStock(@PathVariable Long id,
                                           @RequestParam Integer cantidad) {
        Optional<IngredienteDTO> opt = service.aumentarStock(id, cantidad);
        if (opt.isPresent()) return ResponseEntity.ok(opt.get());
        return ResponseEntity.notFound().build();
    }
}
/*
package com.microservicio.inventario.Controller;

import com.microservicio.inventario.Entities.Ingrediente;
import com.microservicio.inventario.Services.IngredienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingredientes")
public class IngredienteController {

    final private IngredienteService service;

    public IngredienteController(IngredienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Ingrediente ingrediente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(ingrediente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Ingrediente ingrediente) {
        Optional<Ingrediente> opt = service.update(id, ingrediente);
        if (opt.isPresent()) return ResponseEntity.ok(opt.get());
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = service.deleteById(id);
        if (deleted) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    // PATCH /api/ingredientes/{id}/reducir?cantidad=5
    @PatchMapping("/{id}/reducir")
    public ResponseEntity<?> reducirStock(@PathVariable Long id,
                                          @RequestParam Integer cantidad) {
        try {
            Optional<Ingrediente> opt = service.reducirStock(id, cantidad);
            if (opt.isPresent()) return ResponseEntity.ok(opt.get());
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PATCH /api/ingredientes/{id}/aumentar?cantidad=10
    @PatchMapping("/{id}/aumentar")
    public ResponseEntity<?> aumentarStock(@PathVariable Long id,
                                           @RequestParam Integer cantidad) {
        Optional<Ingrediente> opt = service.aumentarStock(id, cantidad);
        if (opt.isPresent()) return ResponseEntity.ok(opt.get());
        return ResponseEntity.notFound().build();
    }
}

 */