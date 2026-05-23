package com.microservicio.Proveedor.Controller;
import com.microservicio.Proveedor.dto.OrdenCompraRequestDTO;
import com.microservicio.Proveedor.Services.orden_compra.OrdenCompraReadService;
import com.microservicio.Proveedor.Services.orden_compra.OrdenCompraWriteService;
import com.microservicio.Proveedor.Services.proveedor.ProveedorServiceRead;
import com.microservicio.Proveedor.Services.proveedor.ProveedorServiceWrite;
import com.microservicio.Proveedor.dto.ProveedorDTO;
import com.microservicio.Proveedor.dto.ProveedorRequestDTO;
import com.microservicio.Proveedor.Entities.EstadoOrden;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private  OrdenCompraReadService ordenCompraRead;
    @Autowired
    private  OrdenCompraWriteService ordenCompraWrite;
    @Autowired
    private  ProveedorServiceRead proveedorRead;
    @Autowired
    private  ProveedorServiceWrite proveedorWrite;
    // ============ PROVEEDORES CRUD ============

    @GetMapping("/all")
    public ResponseEntity<?> getAllProveedores() {
        return ResponseEntity.ok(proveedorRead.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProveedorById(@PathVariable Integer id) {
        var proveedor = proveedorRead.findById(id);
        return ResponseEntity.ok(proveedor);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> createProveedor(@Valid @RequestBody ProveedorRequestDTO proveedorDTO) {
        ProveedorDTO saved = proveedorWrite.save(proveedorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProveedor(@PathVariable Integer id,
                                             @Valid @RequestBody ProveedorRequestDTO proveedorDTO) {
        ProveedorDTO updated = proveedorWrite.update(id, proveedorDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProveedor(@PathVariable Integer id) {
        proveedorWrite.delete(id);
        return ResponseEntity.ok(Map.of("message", "Proveedor eliminado exitosamente"));
    }

    // ============ ÓRDENES DE COMPRA ============

    @GetMapping("/{id}/ordenes")
    public ResponseEntity<?> getOrdenesByProveedor(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenCompraRead.findByProveedor(id));
    }

    @GetMapping("/ordenes")
    public ResponseEntity<?> getAllOrdenes() {
        return ResponseEntity.ok(ordenCompraRead.findAll());
    }

    @GetMapping("/ordenes/{ordenId}")
    public ResponseEntity<?> getOrdenById(@PathVariable Integer ordenId) {
        var orden = ordenCompraRead.findById(ordenId);
        return ResponseEntity.ok(orden);
    }

    @PostMapping("/ordenes")
    public ResponseEntity<?> createOrden(@RequestBody Map<String, Integer> request) {
        Integer proveedorId = request.get("proveedorId");

        if (proveedorId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El campo 'proveedorId' es requerido"));
        }

        OrdenCompraRequestDTO requestDTO = new OrdenCompraRequestDTO();
        requestDTO.setProveedorId(proveedorId);
        var created = ordenCompraWrite.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/ordenes/{ordenId}/estado")
    public ResponseEntity<?> updateEstado(@PathVariable Integer ordenId,
                                          @RequestBody Map<String, String> request) {
        EstadoOrden estado = EstadoOrden.valueOf(request.get("estado"));
        var updated = ordenCompraWrite.updateEstado(ordenId, estado);
        return ResponseEntity.ok(updated);
    }

    @PostMapping(value = "/ordenes/{ordenId}/factura", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirFactura(@PathVariable Integer ordenId,
                                          @RequestParam("factura") MultipartFile factura) {
        var updated = ordenCompraWrite.subirFactura(ordenId, factura);
        return ResponseEntity.ok(updated);

    }

    @GetMapping(value = "/ordenes/{ordenId}/factura")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable Integer ordenId) {
        byte[] factura = ordenCompraRead.descargarFactura(ordenId);
        var orden = ordenCompraRead.findById(ordenId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(orden.getFacturaTipo()))
                .header("Content-Disposition", "attachment; filename=\"" + orden.getFacturaNombre() + "\"")
                .body(factura);
    }

    @DeleteMapping("/ordenes/{ordenId}/factura")
    public ResponseEntity<?> eliminarFactura(@PathVariable Integer ordenId) {
        ordenCompraWrite.eliminarFactura(ordenId);
        return ResponseEntity.ok(Map.of("message", "Factura eliminada exitosamente"));
    }

    @DeleteMapping("/ordenes/{ordenId}")
    public ResponseEntity<?> deleteOrden(@PathVariable Integer ordenId) {
        ordenCompraWrite.delete(ordenId);
        return ResponseEntity.ok(Map.of("message", "Orden eliminada exitosamente"));
    }
}