package com.microservicio.pedidos.service;

import com.microservicio.pedidos.dto.*;
import com.microservicio.pedidos.entity.EstadoPedido;
import com.microservicio.pedidos.entity.Pedido;
import com.microservicio.pedidos.entity.PedidoItem;
import com.microservicio.pedidos.mapper.PedidoMapper;
import com.microservicio.pedidos.repository.PedidoItemRepository;
import com.microservicio.pedidos.repository.PedidoRepository;
import com.microservicio.pedidos.service.feign.MesaFeignClient;
import com.microservicio.pedidos.service.feign.ProductoFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final PedidoMapper pedidoMapper;
    private final MesaFeignClient mesaFeignClient;
    private final ProductoFeignClient productoFeignClient;

    public PedidoService(PedidoRepository pedidoRepository,
                         PedidoItemRepository pedidoItemRepository,
                         PedidoMapper pedidoMapper,
                         MesaFeignClient mesaFeignClient,
                         ProductoFeignClient productoFeignClient) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.pedidoMapper = pedidoMapper;
        this.mesaFeignClient = mesaFeignClient;
        this.productoFeignClient = productoFeignClient;
    }

    private String generarOrdenId() {
        return "ORD-" + System.currentTimeMillis();
    }

    private String obtenerHoraActual() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Transactional
    public PedidoResponseDTO crearPedido(CrearPedidoRequestDTO request) {

        // 1. VALIDAR STOCK
        for (PedidoItemRequestDTO itemReq : request.getItems()) {
            ProductoFeignClient.StockResponse stockResponse = productoFeignClient.obtenerStockProducto(itemReq.getProductoId());
            Integer stockDisponible = stockResponse.getStock();

            if (stockDisponible == null || stockDisponible < itemReq.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto ID: " + itemReq.getProductoId() +
                        ". Disponible: " + stockDisponible + ", Solicitado: " + itemReq.getCantidad());
            }
        }

        // 2. OBTENER INFORMACIÓN COMPLETA DE CADA PRODUCTO
        for (PedidoItemRequestDTO itemReq : request.getItems()) {
            ProductoResponseDTO producto = productoFeignClient.obtenerProductoPorId(itemReq.getProductoId());
            if (producto == null) {
                throw new RuntimeException("Producto no encontrado con ID: " + itemReq.getProductoId());
            }
            itemReq.setNombre(producto.getNombre());
            itemReq.setPrecio(producto.getPrecio());
        }

        // 3. CREAR PEDIDO
        String ordenId = generarOrdenId();
        String hora = obtenerHoraActual();

        Pedido pedido = new Pedido(ordenId, request.getMesaNumero(), hora, EstadoPedido.PENDIENTE);
        Pedido savedPedido = pedidoRepository.save(pedido);

        // 4. CREAR ITEMS
        List<PedidoItem> items = request.getItems().stream()
                .map(item -> {
                    PedidoItem pedidoItem = new PedidoItem(
                            item.getProductoId(),
                            item.getNombre(),
                            item.getPrecio(),
                            item.getCantidad(),
                            item.getNotas()
                    );
                    pedidoItem.setPedido(savedPedido);
                    return pedidoItem;
                })
                .collect(Collectors.toList());

        pedidoItemRepository.saveAll(items);
        savedPedido.setItems(items);

        // 5. CALCULAR TOTAL
        double total = items.stream()
                .mapToDouble(i -> i.getPrecio() * i.getCantidad())
                .sum();

        // 6. ACTUALIZAR MESA
        actualizarMesa(request.getMesaNumero().longValue(), total, ordenId);

        // 7. ✅ DESCOTAR STOCK
        descontarStock(request.getItems());  // ← Aquí pasas los items del request

        return pedidoMapper.toResponseDTO(savedPedido);
    }

    private void actualizarMesa(Long mesaId, double total, String ordenId) {
        try {
            // Actualizar estado a OCUPADO
            ActualizarEstadoMesaRequestDTO estadoRequest = new ActualizarEstadoMesaRequestDTO();
            estadoRequest.setEstado("OCUPADO");
            estadoRequest.setOrdenActualId(ordenId);
            mesaFeignClient.actualizarEstadoMesa(mesaId, estadoRequest);

            // Actualizar total
            ActualizarTotalMesaRequestDTO totalRequest = new ActualizarTotalMesaRequestDTO();
            totalRequest.setTotal(total);
            mesaFeignClient.actualizarTotalMesa(mesaId, totalRequest);

        } catch (Exception e) {
            System.err.println("Error al actualizar mesa " + mesaId + ": " + e.getMessage());
        }
    }

    private void liberarMesa(Long mesaId) {
        try {
            ActualizarEstadoMesaRequestDTO estadoRequest = new ActualizarEstadoMesaRequestDTO();
            estadoRequest.setEstado("DISPONIBLE");
            estadoRequest.setTotalActual(0.0);
            estadoRequest.setOrdenActualId(null);
            mesaFeignClient.actualizarEstadoMesa(mesaId, estadoRequest);
        } catch (Exception e) {
            System.err.println("Error al liberar mesa " + mesaId + ": " + e.getMessage());
        }
    }

    public List<PedidoResponseDTO> getAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PedidoResponseDTO getPedidoById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        return pedidoMapper.toResponseDTO(pedido);
    }

    public PedidoResponseDTO getPedidoByOrdenId(String ordenId) {
        Pedido pedido = pedidoRepository.findByOrdenId(ordenId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ordenId: " + ordenId));
        return pedidoMapper.toResponseDTO(pedido);
    }

    public List<PedidoResponseDTO> getPedidosByEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponseDTO actualizarEstado(Long id, ActualizarEstadoRequestDTO request) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        pedido.setEstado(request.getEstado());
        pedido.setUpdatedAt(LocalDateTime.now());

        if (request.getEstado() == EstadoPedido.COMPLETADO) {
            liberarMesa(pedido.getMesaNumero().longValue());
        }

        Pedido updatedPedido = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDTO(updatedPedido);
    }

    @Transactional
    public PedidoItemResponseDTO actualizarItemCompletado(Long itemId, ActualizarItemCompletadoRequestDTO request) {
        PedidoItem item = pedidoItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado con ID: " + itemId));

        item.setCompletado(request.getCompletado());
        PedidoItem updatedItem = pedidoItemRepository.save(item);

        return PedidoItemResponseDTO.builder()
                .id(updatedItem.getId())
                .productoId(updatedItem.getProductoId())
                .nombre(updatedItem.getNombre())
                .precio(updatedItem.getPrecio())
                .cantidad(updatedItem.getCantidad())
                .notas(updatedItem.getNotas())
                .completado(updatedItem.getCompletado())
                .build();
    }

    @Transactional
    public void eliminarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        liberarMesa(pedido.getMesaNumero().longValue());
        pedidoRepository.delete(pedido);
    }

    private void descontarStock(List<PedidoItemRequestDTO> items) {
        for (PedidoItemRequestDTO item : items) {
            try {
                // Obtener stock actual
                ProductoFeignClient.StockResponse stockResponse = productoFeignClient.obtenerStockProducto(item.getProductoId());
                Integer stockActual = stockResponse.getStock();
                Integer nuevoStock = stockActual - item.getCantidad();

                // Validar que no quede negativo
                if (nuevoStock < 0) {
                    System.err.println("⚠️ Stock negativo para producto " + item.getProductoId() + ". Se deja en 0.");
                    nuevoStock = 0;
                }

                // Actualizar stock
                Map<String, Integer> stockRequest = new HashMap<>();
                stockRequest.put("stock", nuevoStock);
                productoFeignClient.actualizarStock(item.getProductoId(), stockRequest);

                System.out.println("✅ Stock actualizado: Producto " + item.getProductoId() +
                        " | " + stockActual + " → " + nuevoStock);
            } catch (Exception e) {
                System.err.println("❌ Error al actualizar stock del producto " + item.getProductoId() + ": " + e.getMessage());
            }
        }
    }
}