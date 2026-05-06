package com.microservicio.inventario.Controller;

import com.microservicio.inventario.Services.MermaInsumosService;
import com.microservicio.inventario.dto.CrearMermaDTO;
import com.microservicio.inventario.dto.MermaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/mermas")
public class MermaInsumosController {

    private final MermaInsumosService service;

    public MermaInsumosController(MermaInsumosService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable Long id) {
        Optional<MermaDTO> opt = service.findById(id);
        if (opt.isPresent()) return ResponseEntity.ok(opt.get());
        return ResponseEntity.notFound().build();
    }

    // GET /api/mermas/ingrediente/{ingredienteId}
    @GetMapping("/ingrediente/{ingredienteId}")
    public ResponseEntity<?> listByIngrediente(@PathVariable Long ingredienteId) {
        return ResponseEntity.ok(service.findByIngrediente(ingredienteId));
    }

    // POST /api/mermas/ingrediente/{ingredienteId}
    @PostMapping("/ingrediente/{ingredienteId}")
    public ResponseEntity<?> create(@PathVariable Long ingredienteId,
                                    @RequestBody CrearMermaDTO merma) {
        Optional<MermaDTO> opt = service.save(ingredienteId, merma);
        if (opt.isPresent()) return ResponseEntity.status(HttpStatus.CREATED).body(opt.get());
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = service.deleteById(id);
        if (deleted) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}

/*
package com.microservicio.inventario.Controller;

import com.microservicio.inventario.Entities.MermaInsumos;
import com.microservicio.inventario.Services.MermaInsumosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/mermas")
public class MermaInsumosController {

    final private MermaInsumosService service;

    public MermaInsumosController(MermaInsumosService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> details(@PathVariable Long id) {
        Optional<MermaInsumos> opt = service.findById(id);
        if (opt.isPresent()) return ResponseEntity.ok(opt.get());
        return ResponseEntity.notFound().build();
    }

    // GET /api/mermas/ingrediente/{ingredienteId}
    @GetMapping("/ingrediente/{ingredienteId}")
    public ResponseEntity<?> listByIngrediente(@PathVariable Long ingredienteId) {
        return ResponseEntity.ok(service.findByIngrediente(ingredienteId));
    }

    // POST /api/mermas/ingrediente/{ingredienteId}
    @PostMapping("/ingrediente/{ingredienteId}")
    public ResponseEntity<?> create(@PathVariable Long ingredienteId,
                                    @RequestBody MermaInsumos merma) {
        Optional<MermaInsumos> opt = service.save(ingredienteId, merma);
        if (opt.isPresent()) return ResponseEntity.status(HttpStatus.CREATED).body(opt.get());
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = service.deleteById(id);
        if (deleted) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}


 */