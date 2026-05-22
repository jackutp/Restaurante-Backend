package com.microservicio.pedidos.mapper;

import com.microservicio.pedidos.dto.PedidoItemResponseDTO;
import com.microservicio.pedidos.dto.PedidoResponseDTO;
import com.microservicio.pedidos.entity.Pedido;
import com.microservicio.pedidos.entity.PedidoItem;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class PedidoMapper {

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        if (pedido == null) return null;

        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .ordenId(pedido.getOrdenId())
                .mesaNumero(pedido.getMesaNumero())
                .hora(pedido.getHora())
                .estado(pedido.getEstado())
                .items(pedido.getItems().stream()
                        .map(this::toItemResponseDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    public PedidoItemResponseDTO toItemResponseDTO(PedidoItem item) {
        if (item == null) return null;

        return PedidoItemResponseDTO.builder()
                .id(item.getId())
                .productoId(item.getProductoId())
                .nombre(item.getNombre())
                .precio(item.getPrecio())
                .cantidad(item.getCantidad())
                .notas(item.getNotas())
                .completado(item.getCompletado())
                .build();
    }
}