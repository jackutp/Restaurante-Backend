package com.microservicio.pedidos.service.feign;

import com.microservicio.pedidos.dto.CrearPedidoCocinaRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "microservicio-cocina", url = "http://localhost:8218")
public interface CocinaFeignClient {

    @PostMapping("/cocina/pedidos")
    void enviarPedidoACocina(@RequestBody CrearPedidoCocinaRequestDTO request);
}