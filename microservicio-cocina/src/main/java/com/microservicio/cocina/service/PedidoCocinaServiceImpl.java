package com.microservicio.cocina.service;

import com.microservicio.cocina.dto.*;
import com.microservicio.cocina.entity.ItemCocina;
import com.microservicio.cocina.entity.PedidoCocina;
import com.microservicio.cocina.mapper.PedidoCocinaMapper;
import com.microservicio.cocina.repository.ItemCocinaRepository;
import com.microservicio.cocina.repository.PedidoCocinaRepository;
import com.microservicio.cocina.service.feign.PedidoFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedidoCocinaServiceImpl implements PedidoCocinaService {

    private final PedidoCocinaRepository pedidoRepository;
    private final ItemCocinaRepository itemRepository;
    private final PedidoCocinaMapper mapper;
    private final PedidoFeignClient pedidoFeignClient;

    public PedidoCocinaServiceImpl(PedidoCocinaRepository pedidoRepository,
                                   ItemCocinaRepository itemRepository,
                                   PedidoCocinaMapper mapper,
                                   PedidoFeignClient pedidoFeignClient) {
        this.pedidoRepository = pedidoRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
        this.pedidoFeignClient = pedidoFeignClient;
    }

    @Override
    @Transactional
    public PedidoCocinaResponseDTO recibirPedido(CrearPedidoCocinaRequestDTO request) {

        PedidoCocina pedido = new PedidoCocina(
                request.getOrdenId(),
                request.getMesaNumero(),
                request.getHora()
        );

        List<ItemCocina> items = request.getItems().stream()
                .map(item -> new ItemCocina(
                        item.getProductoId(),
                        item.getNombre(),
                        item.getCantidad(),
                        item.getNotas()
                ))
                .collect(Collectors.toList());

        items.forEach(item -> item.setPedido(pedido));
        pedido.setItems(items);

        PedidoCocina saved = pedidoRepository.save(pedido);
        return mapper.toResponseDTO(saved);
    }

    @Override
    public List<PedidoCocinaResponseDTO> getPedidosPendientes() {
        return pedidoRepository.findByEstado("PENDIENTE").stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PedidoCocinaResponseDTO getPedidoByOrdenId(String ordenId) {
        PedidoCocina pedido = pedidoRepository.findByOrdenId(ordenId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + ordenId));
        return mapper.toResponseDTO(pedido);
    }

    @Override
    @Transactional
    public ItemCocinaResponseDTO marcarItemCompletado(Long itemId) {
        ItemCocina item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + itemId));

        item.setCompletado(true);
        itemRepository.save(item);

        PedidoCocina pedido = item.getPedido();
        boolean todosCompletados = pedido.getItems().stream().allMatch(ItemCocina::getCompletado);

        if (todosCompletados) {
            pedido.setEstado("LISTO");
            pedidoRepository.save(pedido);
        }

        return mapper.toItemResponseDTO(item);
    }

    @Override
    @Transactional
    public void marcarPedidoServido(String ordenId) {
        PedidoCocina pedido = pedidoRepository.findByOrdenId(ordenId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + ordenId));

        pedido.setEstado("SERVIDO");
        pedidoRepository.save(pedido);

        Map<String, String> request = new HashMap<>();
        request.put("estado", "SERVIDO");
        pedidoFeignClient.actualizarEstadoPedido(ordenId, request);
    }

    @Override
    public List<PedidoCocina> getHistorialPedidos() {
        return pedidoRepository.findByEstadoIn(Arrays.asList("LISTO", "SERVIDO"));
    }
}