package com.microservicio.inventario.Services;

import com.microservicio.inventario.Entities.Producto;
import com.microservicio.inventario.Mapper.InventarioMapper;
import com.microservicio.inventario.Repositories.ProductoRepository;
import com.microservicio.inventario.dto.CrearProductoDTO;
import com.microservicio.inventario.dto.ProductoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repository;
    private final InventarioMapper mapper;

    public ProductoServiceImpl(ProductoRepository repository, InventarioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> findAll() {
        List<Producto> productos = (List<Producto>) repository.findAll();
        return productos.stream()
                .map(mapper::toProductoDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoDTO> findById(Long id) {
        return repository.findById(id).map(mapper::toProductoDTO);
    }

    @Override
    @Transactional
    public ProductoDTO save(CrearProductoDTO productoDTO) {
        Producto producto = Producto.builder()
                .nombre(productoDTO.getNombre())
                .descripcion(productoDTO.getDescripcion())
                .precio(productoDTO.getPrecio())
                .categoria(productoDTO.getCategoria())
                .build();
        Producto saved = repository.save(producto);
        return mapper.toProductoDTO(saved);
    }

    @Override
    @Transactional
    public Optional<ProductoDTO> update(Long id, CrearProductoDTO productoDTO) {
        return repository.findById(id).map(existing -> {
            existing.setNombre(productoDTO.getNombre());
            existing.setDescripcion(productoDTO.getDescripcion());
            existing.setPrecio(productoDTO.getPrecio());
            existing.setCategoria(productoDTO.getCategoria());
            return mapper.toProductoDTO(repository.save(existing));
        });
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return repository.findById(id).map(p -> {
            repository.delete(p);
            return true;
        }).orElse(false);
    }
}

/*
package com.microservicio.inventario.Services;

import com.microservicio.inventario.Entities.Producto;
import com.microservicio.inventario.Repositories.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    final private ProductoRepository repository;

    public ProductoServiceImpl(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return (List<Producto>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findById(Long id) {
        return repository.findById(id); // ← estaba vacío, corregido
    }

    @Override
    @Transactional
    public Producto save(Producto producto) {
        return repository.save(producto);
    }

    @Override
    @Transactional
    public Optional<Producto> update(Long id, Producto producto) {
        return repository.findById(id).map(existing -> {
            existing.setNombre(producto.getNombre());
            existing.setDescripcion(producto.getDescripcion());
            existing.setPrecio(producto.getPrecio());
            existing.setCategoria(producto.getCategoria());
            return repository.save(existing);
        });
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return repository.findById(id).map(p -> {
            repository.delete(p);
            return true;
        }).orElse(false);
    }
}

 */