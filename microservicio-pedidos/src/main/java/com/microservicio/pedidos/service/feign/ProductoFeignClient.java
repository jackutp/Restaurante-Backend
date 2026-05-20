package com.microservicio.pedidos.service.feign;

import com.microservicio.pedidos.dto.ProductoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "microservicio-producto", url = "http://localhost:8080")
public interface ProductoFeignClient {

    @GetMapping("/productos/{id}")
    ProductoResponseDTO obtenerProductoPorId(@PathVariable("id") Integer id);

    @GetMapping("/productos/{id}/stock")
    StockResponse obtenerStockProducto(@PathVariable("id") Integer id);

    // ✅ AGREGAR ESTE MÉTODO
    @PatchMapping("/productos/{id}/stock")
    void actualizarStock(@PathVariable("id") Integer id,
                         @RequestBody Map<String, Integer> request);

    class StockResponse {
        private Integer stock;

        public StockResponse() {}

        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }
}