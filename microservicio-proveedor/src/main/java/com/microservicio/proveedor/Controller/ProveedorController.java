package com.microservicio.proveedor.Controller;
import com.microservicio.proveedor.Entities.proveedor;
import com.microservicio.proveedor.Services.ProveedorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@RestController
@RequestMapping("/proveedores")
public class ProveedorController {
    final private ProveedorService proveedorService;
    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public ResponseEntity<?> list(){
        return ResponseEntity.ok(this.proveedorService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?>details (@PathVariable Integer id){
        Optional<proveedor>proveedorOptional=proveedorService.findById(id);
        if (proveedorOptional.isPresent()){
            return ResponseEntity.ok(proveedorOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping
    public ResponseEntity<?> save(@RequestBody proveedor proveedor) {
        try {
            proveedor nuevoProveedor = proveedorService.save(proveedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody proveedor proveedor) {
        try {
            proveedor proveedorActualizado = proveedorService.update(id, proveedor);
            return ResponseEntity.ok(proveedorActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            proveedorService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
