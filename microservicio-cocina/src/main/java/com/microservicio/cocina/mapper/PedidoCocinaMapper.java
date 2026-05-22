package com.microservicio.cocina.mapper;
import com.microservicio.cocina.dto.*;
import com.microservicio.cocina.entity.ItemCocina;
import com.microservicio.cocina.entity.PedidoCocina;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
@Component
public class PedidoCocinaMapper {
    public PedidoCocinaResponseDTO toResponseDTO(PedidoCocina pedido) {
        if (pedido == null) return null;
        PedidoCocinaResponseDTO dto = new PedidoCocinaResponseDTO();
        dto.setId(pedido.getId());
        dto.setOrdenId(pedido.getOrdenId());
        dto.setMesaNumero(pedido.getMesaNumero());
        dto.setHora(pedido.getHora());
        dto.setEstado(pedido.getEstado());
        dto.setItems(pedido.getItems().stream()
                .map(this::toItemResponseDTO)
                .collect(Collectors.toList()));
        return dto;
    }
    public ItemCocinaResponseDTO toItemResponseDTO(ItemCocina item) {
        if (item == null) return null;
        ItemCocinaResponseDTO dto = new ItemCocinaResponseDTO();
        dto.setId(item.getId());
        dto.setProductoId(item.getProductoId());
        dto.setNombre(item.getNombre());
        dto.setCantidad(item.getCantidad());
        dto.setNotas(item.getNotas());
        dto.setCompletado(item.getCompletado());
        return dto;
    }
}