package com.microservicio.Producto.Services;

import com.microservicio.Producto.Entities.Categoria;
import com.microservicio.Producto.Entities.Producto;
import com.microservicio.Producto.Mapper.ProductoMapper;
import com.microservicio.Producto.Repositories.ProductoRepository;
import com.microservicio.Producto.aws.StorageService;
import com.microservicio.Producto.dto.ProductoDTO;
import com.microservicio.Producto.exception.FileStorageException;
import com.microservicio.Producto.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServiceReadImp implements ProductoServiceRead{
    @Autowired
    private  ProductoRepository productoRepository;
    @Autowired
    private  ProductoMapper productoMapper;
    @Autowired
    private StorageService storageService;

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
    public ProductoDTO findById(Integer id) {
        return productoRepository.findById(id).map(productoMapper::toDTO).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getImagen(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (producto.getImagenProducto() == null) {
            throw new ResourceNotFoundException("El producto no tiene imagen asociada");
        }
        try {
            return storageService.getFile(producto.getImagenProducto());
        } catch (IOException e) {
            throw new FileStorageException("No se pudo encontrar la imagen: " + e);
        }
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

    @Override
    public Integer getStock(Integer id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return producto.getStock();
    }
}
