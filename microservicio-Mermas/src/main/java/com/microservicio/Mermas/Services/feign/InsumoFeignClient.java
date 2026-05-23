package com.microservicio.Mermas.Services.feign;

import com.microservicio.Mermas.dto.InsumoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "microservicio-insumos")
public interface InsumoFeignClient {

    @GetMapping("/insumos/all")
    List<InsumoDTO> getAllInsumos();

    @GetMapping("/insumos/{id}")
    InsumoDTO getInsumoById(@PathVariable("id") Integer id);
}