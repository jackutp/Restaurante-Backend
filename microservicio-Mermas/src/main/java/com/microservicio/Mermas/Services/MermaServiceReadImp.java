package com.microservicio.Mermas.Services;

import com.microservicio.Mermas.Entities.TipoMerma;
import com.microservicio.Mermas.Mapper.MermaMapper;
import com.microservicio.Mermas.Repositories.MermaRepository;
import com.microservicio.Mermas.Services.feign.InsumoFeignClient;
import com.microservicio.Mermas.Services.feign.ProductoFeignClient;
import com.microservicio.Mermas.dto.InsumoDTO;
import com.microservicio.Mermas.dto.MermaDTO;
import com.microservicio.Mermas.dto.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MermaServiceReadImp implements MermaServiceRead{
    @Autowired
    private  MermaRepository mermaRepository;
    @Autowired
    private  MermaMapper mermaMapper;
    @Autowired
    private  ProductoFeignClient productoFeignClient;
    @Autowired
    private  InsumoFeignClient insumoFeignClient;

    @Override
    @Transactional(readOnly = true)
    public List<MermaDTO> findAll() {
        return mermaRepository.findAll()
                .stream()
                .map(mermaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MermaDTO> findById(Integer id) {
        return mermaRepository.findById(id).map(mermaMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MermaDTO> findByTipo(TipoMerma tipo) {
        return mermaRepository.findByTipoMerma(tipo)
                .stream()
                .map(mermaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> getProductos() {
        return productoFeignClient.getAllProductos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoDTO> getInsumos() {
        return insumoFeignClient.getAllInsumos();
    }
}
