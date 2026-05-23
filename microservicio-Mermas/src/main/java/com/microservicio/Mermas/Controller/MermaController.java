package com.microservicio.Mermas.Controller;

import com.microservicio.Mermas.Entities.TipoMerma;
import com.microservicio.Mermas.Services.MermaServiceRead;
import com.microservicio.Mermas.Services.MermaServiceWrite;
import com.microservicio.Mermas.dto.MermaDTO;
import com.microservicio.Mermas.dto.MermaRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mermas")
public class MermaController {
    @Autowired
    private  MermaServiceRead mermaRead;
    @Autowired
    private  MermaServiceWrite mermaWrite;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(mermaRead.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        var merma = mermaRead.findById(id);
        return ResponseEntity.ok(merma);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> getByTipo(@PathVariable TipoMerma tipo) {
        return ResponseEntity.ok(mermaRead.findByTipo(tipo));
    }

    @GetMapping("/productos")
    public ResponseEntity<?> getProductos() {
        return ResponseEntity.ok(mermaRead.getProductos());
    }

    @GetMapping("/insumos")
    public ResponseEntity<?> getInsumos() {
        return ResponseEntity.ok(mermaRead.getInsumos());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MermaRequestDTO mermaDTO) {
        MermaDTO saved = mermaWrite.save(mermaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody MermaRequestDTO mermaDTO) {
        MermaDTO updated = mermaWrite.update(id, mermaDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        mermaWrite.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Merma eliminada exitosamente");
        return ResponseEntity.ok(response);
    }
}