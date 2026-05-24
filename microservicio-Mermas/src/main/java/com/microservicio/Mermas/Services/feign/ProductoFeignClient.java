package com.microservicio.Mermas.Services.feign;

import com.microservicio.Mermas.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "microservicio-producto")
public interface ProductoFeignClient {

    @GetMapping("/productos/all")
    List<ProductoDTO> getAllProductos();

    @GetMapping("/productos/{id}")
    ProductoDTO getProductoById(@PathVariable("id") Integer id);
}