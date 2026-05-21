package com.microservicio.pagos.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "microservicio-mesas", url = "http://localhost:8216")
public interface MesaFeignClient {

    @PutMapping("/mesas/numero/{numero}/estado")
    void liberarMesa(@PathVariable("numero") Integer numero,
                     @RequestBody Map<String, Object> request);

    @PutMapping("/mesas/numero/{numero}/total")
    void resetearTotal(@PathVariable("numero") Integer numero,
                       @RequestBody Map<String, Double> request);
}