package com.microservicio.Producto.Controller;
import com.microservicio.Producto.Entities.Categoria;
import com.microservicio.Producto.Services.ProductoServiceRead;
import com.microservicio.Producto.Services.ProductoServiceWrite;
import com.microservicio.Producto.dto.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/productos")
public class ProductoController {
    @Autowired
    private ProductoServiceWrite productoWrite;
    @Autowired
    private ProductoServiceRead productoRead;

    // GET: Listar todos los productos
    @GetMapping("/all")
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        return ResponseEntity.ok(productoRead.findAll());
    }
    // GET: Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductoById(@PathVariable Integer id) {
        return ResponseEntity.ok(productoRead.findById(id));
    }
    // GET: Obtener imagen del producto
    @GetMapping(value = "/{id}/imagen", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getImagen(@PathVariable Integer id) {
        byte[] imagen = productoRead.getImagen(id);
        return ResponseEntity.ok().body(imagen);
    }
    // GET: Filtrar por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoDTO>> getByCategoria(@PathVariable Categoria categoria) {
        return ResponseEntity.ok(productoRead.findByCategoria(categoria));
    }
    // GET: Filtrar por rango de precio
    @GetMapping("/precio")
    public ResponseEntity<List<ProductoDTO>> getByPrecioRange(
            @RequestParam Double min,
            @RequestParam Double max) {
        return ResponseEntity.ok(productoRead.findByPrecioRange(min, max));
    }
    // Crear producto con imagen
    @PostMapping(value = "/crear",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam("categoria") Categoria categoria,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setNombre(nombre);
        productoDTO.setDescripcion(descripcion);
        productoDTO.setPrecio(java.math.BigDecimal.valueOf(precio));
        productoDTO.setCategoria(categoria);
        ProductoDTO saved = productoWrite.save(productoDTO, imagen);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    // Actualizar producto completo
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProducto(
            @PathVariable Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam("categoria") Categoria categoria,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setNombre(nombre);
        productoDTO.setDescripcion(descripcion);
        productoDTO.setPrecio(java.math.BigDecimal.valueOf(precio));
        productoDTO.setCategoria(categoria);
        ProductoDTO updated = productoWrite.update(id, productoDTO, imagen);
        return ResponseEntity.ok(updated);
    }
    // Actualizar solo la imagen
    @PutMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateImagen( @PathVariable Integer id, @RequestParam("imagen") MultipartFile imagen) {
        productoWrite.updateImagen(id, imagen);
        return ResponseEntity.ok(Map.of("message", "Imagen actualizada exitosamente"));
    }
    // Eliminar producto (incluye su imagen)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable Integer id) {
        productoWrite.delete(id);
        return ResponseEntity.ok(Map.of("message", "Producto eliminado exitosamente"));
    }
    // Eliminar solo la imagen del producto
    @DeleteMapping("/{id}/imagen")
    public ResponseEntity<?> deleteImagen(@PathVariable Integer id) {
        productoWrite.deleteImagen(id);
        return ResponseEntity.ok(Map.of("message", "Imagen eliminada exitosamente"));
    }
    // MODIFICACION STOCK :V
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateStock( @PathVariable Integer id, @RequestBody Map<String, Integer> request) {
        Integer nuevoStock = request.get("stock");
        ProductoDTO updated = productoWrite.updateStock(id, nuevoStock);
        return ResponseEntity.ok(updated);
    }
    // Obtener stock de un producto
    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getStock(@PathVariable Integer id) {
        Integer stock = productoRead.getStock(id);
        return ResponseEntity.ok(Map.of("stock", stock));
    }
}