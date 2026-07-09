package com.microservicio.Producto.Services;

import com.microservicio.Producto.Entities.Producto;
import com.microservicio.Producto.Mapper.ProductoMapper;
import com.microservicio.Producto.Repositories.ProductoRepository;
import com.microservicio.Producto.aws.StorageService;
import com.microservicio.Producto.dto.ProductoDTO;
import com.microservicio.Producto.exception.FileStorageException;
import com.microservicio.Producto.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class ProductoServiceWriteImp implements ProductoServiceWrite {
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ProductoMapper productoMapper;
    @Autowired
    private StorageService storageService;

    @Override
    @Transactional
    public ProductoDTO save(ProductoDTO productoDTO, MultipartFile imagen) {
        log.info("Registrando producto '{}'", productoDTO.getNombre());
        try {
            // Convertir DTO a Entity
            Producto producto = productoMapper.toEntity(productoDTO);

            // Guardar imagen si existe
            log.info("Subiendo imagen para el producto '{}'", productoDTO.getNombre());
            if (imagen != null && !imagen.isEmpty()) {
                String imagenPath = storageService.uploadFile(imagen);
                producto.setImagenProducto(imagenPath);
                log.info("Imagen subida correctamente");
            }

            // Guardar producto
            Producto saved = productoRepository.save(producto);
            log.info("Producto registrado correctamente (id={}, nombre='{}'", saved.getProductoid(), saved.getNombre());
            return productoMapper.toDTO(saved);

        } catch (IOException e) {
            log.error("Error almacenando imagen para '{}': {}", productoDTO.getNombre(), e.getMessage());
            throw new FileStorageException("Error al guardar el producto: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ProductoDTO update(Integer id, ProductoDTO productoDTO, MultipartFile imagen) {
        log.info("Actualizando producto id={}", id);
        Producto existingProducto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        // Actualizar campos
        existingProducto.setNombre(productoDTO.getNombre());
        existingProducto.setDescripcion(productoDTO.getDescripcion());
        existingProducto.setPrecio(productoDTO.getPrecio());
        existingProducto.setCategoria(productoDTO.getCategoria());
        // Actualizar imagen si se envía una nueva
        if (imagen != null && !imagen.isEmpty()) {
            try {
                // Eliminar imagen anterior si existe
                if (existingProducto.getImagenProducto() != null) {
                    log.info("Reemplazando imagen del producto id={}", id);
                    storageService.deleteFile(existingProducto.getImagenProducto());
                }
                // Guardar nueva imagen
                String imagenPath = storageService.uploadFile(imagen);
                existingProducto.setImagenProducto(imagenPath);
            } catch (IOException e) {
                log.warn("No se pudo actualizando la imagen anterior del producto {}", id);
                throw new FileStorageException("Error al actualizar la imagen: " + e.getMessage());
            }
        }
        Producto updated = productoRepository.save(existingProducto);
        log.info("Producto {} actualizado correctamente", id);
        return productoMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando producto {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        // Eliminar la imagen asociada
        if (producto.getImagenProducto() != null) {
            log.info("Eliminando imagen al producto {}", id);
            storageService.deleteFile(producto.getImagenProducto());
        }

        productoRepository.deleteById(id);
        log.info("Producto {} eliminado correctamente", id);
    }

    @Override
    @Transactional
    public ProductoDTO updateStock(Integer id, Integer nuevoStock) {
        log.info("Actualizando el stock del producto {} a {}", id, nuevoStock);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        // Validar que el stock no sea negativo
        if (nuevoStock < 0) {
            log.warn("Se intentó actualizar el stock a un negativo ({}) al producto {}", nuevoStock, id);
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        producto.setStock(nuevoStock);
        Producto updated = productoRepository.save(producto);

        log.info("Stock actualizado correctamente para el producto {}", id);
        return productoMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void updateImagen(Integer id, MultipartFile imagen) {
        log.info("Actualizando imagen del producto (id={})", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        try {
            // Eliminar imagen anterior
            if (producto.getImagenProducto() != null) {
                storageService.deleteFile(producto.getImagenProducto());
            }
            // Guardar nueva imagen
            String imagenPath = storageService.uploadFile(imagen);
            producto.setImagenProducto(imagenPath);
            productoRepository.save(producto);
            log.info("Imagen actualizada correctamente para el producto {}", id);

        } catch (IOException e) {
            log.error("Error actualizando imagen del producto {}", id);
            throw new FileStorageException("Error al actualizar la imagen: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteImagen(Integer id) {
        log.info("Eliminando imagen del producto {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (producto.getImagenProducto() != null) {
            storageService.deleteFile(producto.getImagenProducto());
            producto.setImagenProducto(null);
            productoRepository.save(producto);
            log.info("Imagen eliminada correctamente del producto {}", id);
        } else {
            log.warn("El producto {} no tenía ninguna imagen asociada", id);
        }
    }
}
