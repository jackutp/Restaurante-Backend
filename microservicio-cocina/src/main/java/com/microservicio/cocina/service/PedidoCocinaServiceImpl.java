package com.microservicio.cocina.service;

import com.microservicio.cocina.dto.*;
import com.microservicio.cocina.entity.ItemCocina;
import com.microservicio.cocina.entity.PedidoCocina;
import com.microservicio.cocina.exception.ConflictException;
import com.microservicio.cocina.exception.ExternalServiceException;
import com.microservicio.cocina.exception.ResourceNotFoundException;
import com.microservicio.cocina.mapper.PedidoCocinaMapper;
import com.microservicio.cocina.repository.ItemCocinaRepository;
import com.microservicio.cocina.repository.PedidoCocinaRepository;
import com.microservicio.cocina.service.feign.PedidoFeignClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedidoCocinaServiceImpl implements PedidoCocinaService {

    @Autowired
    private  PedidoCocinaRepository pedidoRepository;
    @Autowired
    private  ItemCocinaRepository itemRepository;
    @Autowired
    private  PedidoCocinaMapper mapper;
    @Autowired
    private  PedidoFeignClient pedidoFeignClient;

    @Override
    @Transactional
    public PedidoCocinaResponseDTO recibirPedido(CrearPedidoCocinaRequestDTO request) {
        if(request.getItems() == null || request.getItems().isEmpty()){
            throw new ConflictException("El pedido no puede estar vacio");
        }

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
    @Transactional(readOnly = true)
    public List<PedidoCocinaResponseDTO> getPedidosPendientes() {
        return pedidoRepository.findByEstado("PENDIENTE").stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoCocinaResponseDTO getPedidoByOrdenId(String ordenId) {
        PedidoCocina pedido = pedidoRepository.findByOrdenId(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + ordenId));
        return mapper.toResponseDTO(pedido);
    }

    @Override
    @Transactional
    public ItemCocinaResponseDTO marcarItemCompletado(Long itemId) {
        ItemCocina item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado: " + itemId));

        item.setCompletado(true);
        itemRepository.save(item);

        PedidoCocina pedido = item.getPedido();
        boolean todosCompletados = pedido.getItems().stream().allMatch(ItemCocina::getCompletado);

        if (todosCompletados) {
            pedido.setEstado("LISTO");
            pedidoRepository.save(pedido);

            try {
                Map<String, String> request = new HashMap<>();
                request.put("estado", "SERVIDO");
                pedidoFeignClient.actualizarEstadoPedido(pedido.getOrdenId(), request);
                System.out.println("Pedido " + pedido.getOrdenId() + " actualizado a SERVIDO en Pedidos");
            } catch (FeignException e) {
                throw new ExternalServiceException("Servicio Pedidos no disponible");
            }
        }

        return mapper.toItemResponseDTO(item);
    }

    @Override
    @Transactional
    public void marcarPedidoServido(String ordenId) {
        PedidoCocina pedido = pedidoRepository.findByOrdenId(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + ordenId));

        pedido.setEstado("SERVIDO");
        pedidoRepository.save(pedido);

        Map<String, String> request = new HashMap<>();
        request.put("estado", "SERVIDO");
        try{
            pedidoFeignClient.actualizarEstadoPedido(ordenId, request);
        } catch (FeignException e){
            throw new ExternalServiceException("Servicio Pedidos no disponible");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoCocina> getHistorialPedidos() {
        return pedidoRepository.findByEstadoIn(Arrays.asList("LISTO", "SERVIDO"));
    }
}