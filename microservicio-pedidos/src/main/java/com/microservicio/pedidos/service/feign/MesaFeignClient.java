package com.microservicio.pedidos.service.feign;

import com.microservicio.pedidos.dto.ActualizarEstadoMesaRequestDTO;
import com.microservicio.pedidos.dto.ActualizarTotalMesaRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "microservicio-mesas", url = "http://localhost:8216")
public interface MesaFeignClient {

    @PutMapping("/mesas/numero/{numero}/estado")
    void actualizarEstadoMesa(@PathVariable("numero") Integer numero,
                              @RequestBody ActualizarEstadoMesaRequestDTO request);

    @PutMapping("/mesas/numero/{numero}/total")
    void actualizarTotalMesa(@PathVariable("numero") Integer numero,
                             @RequestBody ActualizarTotalMesaRequestDTO request);
}