package com.microservicio.cocina.service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
@FeignClient(name = "microservicio-pedidos")
public interface PedidoFeignClient {
    @PostMapping("/pedidos/orden/{ordenId}/estado")
    void actualizarEstadoPedido(@PathVariable("ordenId") String ordenId,
                                @RequestBody Map<String, String> request);
}