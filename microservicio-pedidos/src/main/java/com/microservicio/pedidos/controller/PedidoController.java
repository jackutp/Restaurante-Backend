package com.microservicio.pedidos.controller;
import com.microservicio.pedidos.dto.*;
import com.microservicio.pedidos.entity.EstadoPedido;
import com.microservicio.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> getAllPedidos() {
        return ResponseEntity.ok(pedidoService.getAllPedidos());
    }
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> getPedidoById(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.getPedidoById(id));
    }
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<PedidoResponseDTO> getPedidoByOrdenId(@PathVariable String ordenId) {
        return ResponseEntity.ok(pedidoService.getPedidoByOrdenId(ordenId));
    }
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosByEstado(@PathVariable EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.getPedidosByEstado(estado));
    }
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(@Valid @RequestBody CrearPedidoRequestDTO request) {
        PedidoResponseDTO newPedido = pedidoService.crearPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPedido);
    }
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequestDTO request) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, request));
    }
    @PatchMapping("/items/{itemId}/completado")
    public ResponseEntity<PedidoItemResponseDTO> actualizarItemCompletado(
            @PathVariable Long itemId,
            @Valid @RequestBody ActualizarItemCompletadoRequestDTO request) {
        return ResponseEntity.ok(pedidoService.actualizarItemCompletado(itemId, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
    //metrica
    @GetMapping("/metricas")
    public ResponseEntity<MetricasPedidosResponseDTO> getMetricas() {
        return ResponseEntity.ok(pedidoService.getMetricas());
    }

    @PostMapping("/orden/{ordenId}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstadoPorOrdenId(
            @PathVariable String ordenId,
            @Valid @RequestBody ActualizarEstadoRequestDTO request) {
        System.out.println("recibido para orden: " + ordenId + " -> " + request.getEstado());
        return ResponseEntity.ok(pedidoService.actualizarEstadoPorOrdenId(ordenId, request));
    }
}