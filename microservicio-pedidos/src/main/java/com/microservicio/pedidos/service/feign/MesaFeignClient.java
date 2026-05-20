package com.microservicio.pedidos.service.feign;

import com.microservicio.pedidos.dto.ActualizarEstadoMesaRequestDTO;
import com.microservicio.pedidos.dto.ActualizarTotalMesaRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "microservicio-mesas", url = "http://localhost:8216")
public interface MesaFeignClient {

    @PutMapping("/mesas/{id}/estado")  // ← Cambiar a PUT
    void actualizarEstadoMesa(@PathVariable("id") Long id,
                              @RequestBody ActualizarEstadoMesaRequestDTO request);

    @PutMapping("/mesas/{id}/total")   // ← Cambiar a PUT
    void actualizarTotalMesa(@PathVariable("id") Long id,
                             @RequestBody ActualizarTotalMesaRequestDTO request);
}