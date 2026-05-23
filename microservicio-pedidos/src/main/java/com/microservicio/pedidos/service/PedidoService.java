package com.microservicio.pedidos.service;

import com.microservicio.pedidos.dto.*;
import com.microservicio.pedidos.entity.EstadoPedido;
import com.microservicio.pedidos.entity.Pedido;
import com.microservicio.pedidos.entity.PedidoItem;
import com.microservicio.pedidos.exception.ConflictException;
import com.microservicio.pedidos.exception.ExternalServiceException;
import com.microservicio.pedidos.exception.ResourceNotFoundException;
import com.microservicio.pedidos.mapper.PedidoMapper;
import com.microservicio.pedidos.repository.PedidoItemRepository;
import com.microservicio.pedidos.repository.PedidoRepository;
import com.microservicio.pedidos.service.feign.MesaFeignClient;
import com.microservicio.pedidos.service.feign.ProductoFeignClient;
import com.microservicio.pedidos.dto.CrearPedidoCocinaRequestDTO;
import com.microservicio.pedidos.dto.ItemCocinaRequestDTO;
import com.microservicio.pedidos.service.feign.CocinaFeignClient;
import feign.FeignException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final PedidoMapper pedidoMapper;
    private final MesaFeignClient mesaFeignClient;
    private final ProductoFeignClient productoFeignClient;
    private final CocinaFeignClient cocinaFeignClient;

    public PedidoService(PedidoRepository pedidoRepository,
                         PedidoItemRepository pedidoItemRepository,
                         PedidoMapper pedidoMapper,
                         MesaFeignClient mesaFeignClient,
                         ProductoFeignClient productoFeignClient,
                         CocinaFeignClient cocinaFeignClient) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.pedidoMapper = pedidoMapper;
        this.mesaFeignClient = mesaFeignClient;
        this.productoFeignClient = productoFeignClient;
        this.cocinaFeignClient = cocinaFeignClient;
    }
    private String generarOrdenId() {
        return "ORD-" + UUID.randomUUID().toString().substring(0,12).toUpperCase();
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
                throw new ConflictException("Stock insuficiente para el producto ID: " + itemReq.getProductoId() +
                        ". Disponible: " + stockDisponible + ", Solicitado: " + itemReq.getCantidad());
            }
        }
        // 2. OBTENER INFORMACIÓN COMPLETA DE CADA PRODUCTO
        for (PedidoItemRequestDTO itemReq : request.getItems()) {
            ProductoResponseDTO producto = productoFeignClient.obtenerProductoPorId(itemReq.getProductoId());
            if (producto == null) {
                throw new ResourceNotFoundException("Producto no encontrado con ID: " + itemReq.getProductoId());
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
        // 6. ACTUALIZAR MESA (AHORA USA NÚMERO, NO ID)
        actualizarMesa(request.getMesaNumero(), total, ordenId);
        // 7. DESCONTAR STOCK
        descontarStock(request.getItems());
        // 8. ENVIAR A COCINA
        enviarPedidoACocina(ordenId, request.getMesaNumero(), hora, request.getItems());
        return pedidoMapper.toResponseDTO(savedPedido);
    }
    // COCINA ENVIAR
    private void enviarPedidoACocina(String ordenId, Integer mesaNumero, String hora, List<PedidoItemRequestDTO> items) {
        try {
            CrearPedidoCocinaRequestDTO cocinaRequest = new CrearPedidoCocinaRequestDTO();
            cocinaRequest.setOrdenId(ordenId);
            cocinaRequest.setMesaNumero(mesaNumero);
            cocinaRequest.setHora(hora);

            List<ItemCocinaRequestDTO> itemsCocina = items.stream().map(item -> {
                ItemCocinaRequestDTO itemDTO = new ItemCocinaRequestDTO();
                itemDTO.setProductoId(item.getProductoId());
                itemDTO.setNombre(item.getNombre());
                itemDTO.setCantidad(item.getCantidad());
                itemDTO.setNotas(item.getNotas());
                return itemDTO;
            }).collect(Collectors.toList());

            cocinaRequest.setItems(itemsCocina);

            cocinaFeignClient.enviarPedidoACocina(cocinaRequest);
            System.out.println("Pedido enviado a cocina: " + ordenId);
        } catch (FeignException e) {
            throw new ExternalServiceException("Error al enviar a cocina " + e.getMessage());
        }
    }
    private void actualizarMesa(Integer numeroMesa, double total, String ordenId) {
        try {
            System.out.println("Enviando a mesa " + numeroMesa + " - Total: " + total);
            // Actualizar estado a OCUPADO
            ActualizarEstadoMesaRequestDTO estadoRequest = new ActualizarEstadoMesaRequestDTO();
            estadoRequest.setEstado("OCUPADO");
            estadoRequest.setTotalActual(total);
            estadoRequest.setOrdenActualId(ordenId);
            mesaFeignClient.actualizarEstadoMesa(numeroMesa, estadoRequest);

                    System.out.println("Mesa actualizada correctamente");
        } catch (FeignException e) {
            throw new ExternalServiceException("Error al actualizar mesa" + numeroMesa + ": " + e.getMessage());
        }
    }

    private void liberarMesa(Integer numeroMesa) {
        try {
            ActualizarEstadoMesaRequestDTO estadoRequest = new ActualizarEstadoMesaRequestDTO();
            estadoRequest.setEstado("DISPONIBLE");
            estadoRequest.setTotalActual(0.0);
            estadoRequest.setOrdenActualId(null);
            mesaFeignClient.actualizarEstadoMesa(numeroMesa, estadoRequest);
        } catch (FeignException e) {
            throw new ExternalServiceException("Error al liberar mesa" + numeroMesa + ": " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> getAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public PedidoResponseDTO getPedidoById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        return pedidoMapper.toResponseDTO(pedido);
    }
    @Transactional(readOnly = true)
    public PedidoResponseDTO getPedidoByOrdenId(String ordenId) {
        Pedido pedido = pedidoRepository.findByOrdenId(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ordenId: " + ordenId));
        return pedidoMapper.toResponseDTO(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> getPedidosByEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponseDTO actualizarEstado(Long id, ActualizarEstadoRequestDTO request) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));

        pedido.setEstado(request.getEstado());
        pedido.setUpdatedAt(LocalDateTime.now());

        // Cuando el pedido se completa, liberar la mesa
        if (request.getEstado() == EstadoPedido.COMPLETADO) {
            liberarMesa(pedido.getMesaNumero());
        }

        Pedido updatedPedido = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDTO(updatedPedido);
    }

    @Transactional
    public PedidoItemResponseDTO actualizarItemCompletado(Long itemId, ActualizarItemCompletadoRequestDTO request) {
        PedidoItem item = pedidoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con ID: " + itemId));

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
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));

        liberarMesa(pedido.getMesaNumero());
        pedidoRepository.delete(pedido);
    }
    private void descontarStock(List<PedidoItemRequestDTO> items) {
        for (PedidoItemRequestDTO item : items) {
            try {
                ProductoFeignClient.StockResponse stockResponse = productoFeignClient.obtenerStockProducto(item.getProductoId());
                Integer stockActual = stockResponse.getStock();
                Integer nuevoStock = stockActual - item.getCantidad();

                if (nuevoStock < 0) {
                    System.err.println("Stock negativo para producto " + item.getProductoId() + ". Se deja en 0.");
                    nuevoStock = 0;
                }

                Map<String, Integer> stockRequest = new HashMap<>();
                stockRequest.put("stock", nuevoStock);
                productoFeignClient.actualizarStock(item.getProductoId(), stockRequest);

                System.out.println("Stock actualizado: Producto " + item.getProductoId() +
                        " | " + stockActual + " → " + nuevoStock);
            } catch (FeignException e) {
                throw new ExternalServiceException("Error al actualizar stock del producto " + item.getProductoId() + ": " + e.getMessage());
            }
        }
    }

    //metricas
    @Transactional(readOnly = true)
    public MetricasPedidosResponseDTO getMetricas() {
        MetricasPedidosResponseDTO metricas = new MetricasPedidosResponseDTO();
        // 1. Órdenes completadas hoy
        Long completadasHoy = pedidoRepository.countOrdenesCompletadasHoy();
        metricas.setOrdenesCompletadas(completadasHoy != null ? completadasHoy : 0L);
        // 2. Órdenes por estado
        List<Object[]> estadoCounts = pedidoRepository.countByEstado();
        Map<String, Long> ordenesPorEstado = new HashMap<>();
        for (Object[] row : estadoCounts) {
            ordenesPorEstado.put(((EstadoPedido) row[0]).toString(), (Long) row[1]);
        }
        metricas.setOrdenesPorEstado(ordenesPorEstado);
        // 3. Productos más vendidos (últimos 7 días)
        LocalDateTime hace7Dias = LocalDateTime.now().minusDays(7);
        Pageable top5 = PageRequest.of(0, 5);
        List<Object[]> topProductos = pedidoItemRepository.findTopProductos(hace7Dias, top5);
        List<MetricasPedidosResponseDTO.ProductoTopDTO> productosTop = new ArrayList<>();
        for (Object[] row : topProductos) {
            productosTop.add(new MetricasPedidosResponseDTO.ProductoTopDTO(
                    (String) row[0],
                    ((Number) row[1]).longValue(),
                    ((Number) row[2]).doubleValue()
            ));
        }
        metricas.setProductosTop(productosTop);
        return metricas;
    }
    @Transactional
    public PedidoResponseDTO actualizarEstadoPorOrdenId(String ordenId, ActualizarEstadoRequestDTO request) {
        Pedido pedido = pedidoRepository.findByOrdenId(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ordenId: " + ordenId));

        pedido.setEstado(request.getEstado());
        pedido.setUpdatedAt(LocalDateTime.now());

        Pedido updatedPedido = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDTO(updatedPedido);
    }
}