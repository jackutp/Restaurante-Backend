package com.microservicio.cocina.service;
import com.microservicio.cocina.dto.*;
import com.microservicio.cocina.entity.PedidoCocina;

import java.util.List;
public interface PedidoCocinaService {
    PedidoCocinaResponseDTO recibirPedido(CrearPedidoCocinaRequestDTO request);
    List<PedidoCocinaResponseDTO> getPedidosPendientes();
    PedidoCocinaResponseDTO getPedidoByOrdenId(String ordenId);
    ItemCocinaResponseDTO marcarItemCompletado(Long itemId);
    void marcarPedidoServido(String ordenId);
    List<PedidoCocina> getHistorialPedidos();
}