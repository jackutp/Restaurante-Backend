package com.microservicio.Producto.Services;

import com.microservicio.Producto.Entities.Categoria;
import com.microservicio.Producto.Entities.Producto;
import com.microservicio.Producto.Mapper.ProductoMapper;
import com.microservicio.Producto.Repositories.ProductoRepository;
import com.microservicio.Producto.Utils.ImageUtils;
import com.microservicio.Producto.dto.ProductoDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceReadImp implements ProductoServiceRead{
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final ImageUtils imageUtils;
    public ProductoServiceReadImp(ProductoRepository productoRepository,
                               ProductoMapper productoMapper,
                               ImageUtils imageUtils) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
        this.imageUtils = imageUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> findAll() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoDTO> findById(Integer id) {
        return productoRepository.findById(id)
                .map(productoMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getImagen(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        if (producto.getImagenProducto() == null) {
            throw new RuntimeException("El producto no tiene imagen asociada");
        }
        return imageUtils.obtenerImagen(producto.getImagenProducto());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> findByCategoria(Categoria categoria) {
        return productoRepository.findByCategoria(categoria)
                .stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> findByPrecioRange(Double min, Double max) {
        return productoRepository.findByPrecioBetween(
                        java.math.BigDecimal.valueOf(min),
                        java.math.BigDecimal.valueOf(max))
                .stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }
}
