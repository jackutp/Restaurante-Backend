package com.microservicio.Producto.Services;

import com.microservicio.Producto.Entities.Producto;
import com.microservicio.Producto.Mapper.ProductoMapper;
import com.microservicio.Producto.Repositories.ProductoRepository;
import com.microservicio.Producto.Utils.ImageUtils;
import com.microservicio.Producto.dto.ProductoDTO;
import com.microservicio.Producto.exception.FileStorageException;
import com.microservicio.Producto.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProductoServiceWriteImp implements ProductoServiceWrite{
    @Autowired
    private  ProductoRepository productoRepository;
    @Autowired
    private  ProductoMapper productoMapper;
    @Autowired
    private  ImageUtils imageUtils;

    @Override
    @Transactional
    public ProductoDTO save(ProductoDTO productoDTO, MultipartFile imagen) {
        try {
            // Convertir DTO a Entity
            Producto producto = productoMapper.toEntity(productoDTO);

            // Guardar imagen si existe
            if (imagen != null && !imagen.isEmpty()) {
                String imagenPath = imageUtils.guardarImagen(imagen);
                producto.setImagenProducto(imagenPath);
            }

            // Guardar producto
            Producto saved = productoRepository.save(producto);
            return productoMapper.toDTO(saved);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el producto: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ProductoDTO update(Integer id, ProductoDTO productoDTO, MultipartFile imagen) {
        Producto existingProducto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));
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
                    imageUtils.eliminarImagen(existingProducto.getImagenProducto());
                }
                // Guardar nueva imagen
                String imagenPath = imageUtils.guardarImagen(imagen);
                existingProducto.setImagenProducto(imagenPath);
            } catch (IOException e) {
                throw new FileStorageException("Error al actualizar la imagen: " + e.getMessage());
            }
        }
        Producto updated = productoRepository.save(existingProducto);
        return productoMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        // Eliminar la imagen asociada
        if (producto.getImagenProducto() != null) {
            imageUtils.eliminarImagen(producto.getImagenProducto());
        }

        productoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductoDTO updateStock(Integer id, Integer nuevoStock) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        // Validar que el stock no sea negativo
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        producto.setStock(nuevoStock);
        Producto updated = productoRepository.save(producto);

        return productoMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void updateImagen(Integer id, MultipartFile imagen) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        try {
            // Eliminar imagen anterior
            if (producto.getImagenProducto() != null) {
                imageUtils.eliminarImagen(producto.getImagenProducto());
            }
            // Guardar nueva imagen
            String imagenPath = imageUtils.guardarImagen(imagen);
            producto.setImagenProducto(imagenPath);
            productoRepository.save(producto);

        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar la imagen: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteImagen(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (producto.getImagenProducto() != null) {
            imageUtils.eliminarImagen(producto.getImagenProducto());
            producto.setImagenProducto(null);
            productoRepository.save(producto);
        }
    }
}
