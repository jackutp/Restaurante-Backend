package com.microservicio.cocina.controller;
import com.microservicio.cocina.dto.*;
import com.microservicio.cocina.entity.PedidoCocina;
import com.microservicio.cocina.mapper.PedidoCocinaMapper;
import com.microservicio.cocina.service.PedidoCocinaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/cocina")
public class PedidoCocinaController {
    private final PedidoCocinaService pedidoCocinaService;
    private final PedidoCocinaMapper pedidoCocinaMapper;
    public PedidoCocinaController(PedidoCocinaService pedidoCocinaService,
                                  PedidoCocinaMapper pedidoCocinaMapper) {
        this.pedidoCocinaService = pedidoCocinaService;
        this.pedidoCocinaMapper = pedidoCocinaMapper;
    }
    @PostMapping("/pedidos")
    public ResponseEntity<PedidoCocinaResponseDTO> recibirPedido(
            @RequestBody CrearPedidoCocinaRequestDTO request) {
        PedidoCocinaResponseDTO pedido = pedidoCocinaService.recibirPedido(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }
    @GetMapping("/pedidos/pendientes")
    public ResponseEntity<List<PedidoCocinaResponseDTO>> getPedidosPendientes() {
        return ResponseEntity.ok(pedidoCocinaService.getPedidosPendientes());
    }
    @GetMapping("/pedidos/{ordenId}")
    public ResponseEntity<PedidoCocinaResponseDTO> getPedidoByOrdenId(
            @PathVariable String ordenId) {
        return ResponseEntity.ok(pedidoCocinaService.getPedidoByOrdenId(ordenId));
    }
    @PatchMapping("/items/{itemId}/completado")
    public ResponseEntity<ItemCocinaResponseDTO> marcarItemCompletado(
            @PathVariable Long itemId) {
        return ResponseEntity.ok(pedidoCocinaService.marcarItemCompletado(itemId));
    }
    @PatchMapping("/pedidos/{ordenId}/servido")
    public ResponseEntity<Void> marcarPedidoServido(@PathVariable String ordenId) {
        pedidoCocinaService.marcarPedidoServido(ordenId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/pedidos/historial")
    public ResponseEntity<List<PedidoCocinaResponseDTO>> getHistorialPedidos() {
        List<PedidoCocina> historial = pedidoCocinaService.getHistorialPedidos();
        return ResponseEntity.ok(historial.stream()
                .map(pedidoCocinaMapper::toResponseDTO)
                .collect(Collectors.toList()));
    }
}