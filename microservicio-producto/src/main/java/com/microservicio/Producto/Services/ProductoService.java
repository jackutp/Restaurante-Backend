package com.microservicio.Producto.Services;
import com.microservicio.Producto.Entities.Categoria;
import com.microservicio.Producto.dto.ProductoDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;
public interface ProductoService {
    List<ProductoDTO> findAll();
    Optional<ProductoDTO> findById(Integer id);
    ProductoDTO save(ProductoDTO productoDTO, MultipartFile imagen);
    ProductoDTO update(Integer id, ProductoDTO productoDTO, MultipartFile imagen);
    void delete(Integer id);
    // actualizar solo stock
    ProductoDTO updateStock(Integer id, Integer nuevoStock);
    // Métodos para imágenes
    byte[] getImagen(Integer id);
    void updateImagen(Integer id, MultipartFile imagen);
    void deleteImagen(Integer id);
    List<ProductoDTO> findByCategoria(Categoria categoria);
    List<ProductoDTO> findByPrecioRange(Double min, Double max);
    Integer getStock(Integer id);
}