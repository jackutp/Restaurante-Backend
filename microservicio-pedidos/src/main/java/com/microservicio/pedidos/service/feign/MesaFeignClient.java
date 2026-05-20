package com.microservicio.pedidos.service.feign;

import com.microservicio.pedidos.dto.ActualizarEstadoMesaRequestDTO;
import com.microservicio.pedidos.dto.ActualizarTotalMesaRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "microservicio-mesas", url = "http://localhost:8216")
public interface MesaFeignClient {

    @PatchMapping("/api/mesas/{id}/estado")  // ← SIN /api
    void actualizarEstadoMesa(@PathVariable("id") Long id,
                              @RequestBody ActualizarEstadoMesaRequestDTO request);

    @PatchMapping("/api/mesas/{id}/total")   // ← SIN /api
    void actualizarTotalMesa(@PathVariable("id") Long id,
                             @RequestBody ActualizarTotalMesaRequestDTO request);
}