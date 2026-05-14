package com.microservicio.Proveedor.Services.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(name = "microservicio-insumos")
public interface InsumoFeignClient {

    @GetMapping("/insumos")
    List<Object> getAllInsumos();

    @PostMapping("/insumos/por-ids")
    List<Object> getInsumosByIds(@RequestBody List<Integer> ids);
}