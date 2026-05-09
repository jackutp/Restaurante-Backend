package com.microservicio.Proveedor.Controller;
import com.microservicio.Proveedor.Services.ProveedorService;
import com.microservicio.Proveedor.Services.OrdenCompraService;
import com.microservicio.Proveedor.dto.OrdenCompraRequestDTO;
import com.microservicio.Proveedor.dto.ProveedorDTO;
import com.microservicio.Proveedor.dto.ProveedorRequestDTO;
import com.microservicio.Proveedor.Entities.EstadoOrden;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final OrdenCompraService ordenCompraService;

    public ProveedorController(ProveedorService proveedorService, OrdenCompraService ordenCompraService) {
        this.proveedorService = proveedorService;
        this.ordenCompraService = ordenCompraService;
    }

    // ============ PROVEEDORES CRUD ============

    @GetMapping
    public ResponseEntity<?> getAllProveedores() {
        return ResponseEntity.ok(proveedorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProveedorById(@PathVariable Integer id) {
        var proveedor = proveedorService.findById(id);
        if (proveedor.isPresent()) {
            return ResponseEntity.ok(proveedor.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Proveedor no encontrado con id: " + id));
        }
    }

    @PostMapping
    public ResponseEntity<?> createProveedor(@Valid @RequestBody ProveedorRequestDTO proveedorDTO) {
        try {
            ProveedorDTO saved = proveedorService.save(proveedorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear proveedor: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProveedor(@PathVariable Integer id,
                                             @Valid @RequestBody ProveedorRequestDTO proveedorDTO) {
        try {
            ProveedorDTO updated = proveedorService.update(id, proveedorDTO);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProveedor(@PathVariable Integer id) {
        try {
            proveedorService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Proveedor eliminado exitosamente"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============ ÓRDENES DE COMPRA ============

    @GetMapping("/{id}/ordenes")
    public ResponseEntity<?> getOrdenesByProveedor(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenCompraService.findByProveedor(id));
    }

    @GetMapping("/ordenes")
    public ResponseEntity<?> getAllOrdenes() {
        return ResponseEntity.ok(ordenCompraService.findAll());
    }

    @GetMapping("/ordenes/{ordenId}")
    public ResponseEntity<?> getOrdenById(@PathVariable Integer ordenId) {
        var orden = ordenCompraService.findById(ordenId);
        if (orden.isPresent()) {
            return ResponseEntity.ok(orden.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Orden no encontrada con id: " + ordenId));
        }
    }

    @PostMapping("/ordenes")
    public ResponseEntity<?> createOrden(@RequestBody Map<String, Integer> request) {
        try {
            Integer proveedorId = request.get("proveedorId");

            if (proveedorId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El campo 'proveedorId' es requerido"));
            }

            OrdenCompraRequestDTO requestDTO = new OrdenCompraRequestDTO();
            requestDTO.setProveedorId(proveedorId);

            var created = ordenCompraService.create(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/ordenes/{ordenId}/estado")
    public ResponseEntity<?> updateEstado(@PathVariable Integer ordenId,
                                          @RequestBody Map<String, String> request) {
        try {
            EstadoOrden estado = EstadoOrden.valueOf(request.get("estado"));
            var updated = ordenCompraService.updateEstado(ordenId, estado);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Estado inválido. Use: PENDIENTE, RECIBIDO, DEVUELTO, CANCELADO"));
        }
    }

    @PostMapping(value = "/ordenes/{ordenId}/factura", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirFactura(@PathVariable Integer ordenId,
                                          @RequestParam("factura") MultipartFile factura) {
        try {
            var updated = ordenCompraService.subirFactura(ordenId, factura);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al subir factura: " + e.getMessage()));
        }
    }

    @GetMapping(value = "/ordenes/{ordenId}/factura")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable Integer ordenId) {
        try {
            byte[] factura = ordenCompraService.descargarFactura(ordenId);
            var orden = ordenCompraService.findById(ordenId).get();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(orden.getFacturaTipo()))
                    .header("Content-Disposition", "attachment; filename=\"" + orden.getFacturaNombre() + "\"")
                    .body(factura);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/ordenes/{ordenId}/factura")
    public ResponseEntity<?> eliminarFactura(@PathVariable Integer ordenId) {
        try {
            ordenCompraService.eliminarFactura(ordenId);
            return ResponseEntity.ok(Map.of("message", "Factura eliminada exitosamente"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/ordenes/{ordenId}")
    public ResponseEntity<?> deleteOrden(@PathVariable Integer ordenId) {
        try {
            ordenCompraService.delete(ordenId);
            return ResponseEntity.ok(Map.of("message", "Orden eliminada exitosamente"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}