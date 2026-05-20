package com.microservicio.Producto.Controller;
import com.microservicio.Producto.Entities.Categoria;
import com.microservicio.Producto.Services.ProductoService;
import com.microservicio.Producto.dto.ProductoDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/productos")
public class ProductoController {
    private final ProductoService productoService;
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }
    // GET: Listar todos los productos
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }
    // GET: Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductoById(@PathVariable Integer id) {
        return productoService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Producto no encontrado con id: " + id)));
    }
    // GET: Obtener imagen del producto
    @GetMapping(value = "/{id}/imagen", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getImagen(@PathVariable Integer id) {
        try {
            byte[] imagen = productoService.getImagen(id);
            return ResponseEntity.ok().body(imagen);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build();
        }
    }
    // GET: Filtrar por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoDTO>> getByCategoria(@PathVariable Categoria categoria) {
        return ResponseEntity.ok(productoService.findByCategoria(categoria));
    }
    // GET: Filtrar por rango de precio
    @GetMapping("/precio")
    public ResponseEntity<List<ProductoDTO>> getByPrecioRange(
            @RequestParam Double min,
            @RequestParam Double max) {
        return ResponseEntity.ok(productoService.findByPrecioRange(min, max));
    }
    // POST: Crear producto con imagen
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam("categoria") Categoria categoria,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        try {
            ProductoDTO productoDTO = new ProductoDTO();
            productoDTO.setNombre(nombre);
            productoDTO.setDescripcion(descripcion);
            productoDTO.setPrecio(java.math.BigDecimal.valueOf(precio));
            productoDTO.setCategoria(categoria);

            ProductoDTO saved = productoService.save(productoDTO, imagen);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear producto: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    // PUT: Actualizar producto completo
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProducto(
            @PathVariable Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam("categoria") Categoria categoria,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        try {
            ProductoDTO productoDTO = new ProductoDTO();
            productoDTO.setNombre(nombre);
            productoDTO.setDescripcion(descripcion);
            productoDTO.setPrecio(java.math.BigDecimal.valueOf(precio));
            productoDTO.setCategoria(categoria);
            ProductoDTO updated = productoService.update(id, productoDTO, imagen);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al actualizar: " + e.getMessage()));
        }
    }
    // PUT: Actualizar solo la imagen
    @PutMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateImagen(
            @PathVariable Integer id,
            @RequestParam("imagen") MultipartFile imagen) {
        try {
            productoService.updateImagen(id, imagen);
            return ResponseEntity.ok(Map.of("message", "Imagen actualizada exitosamente"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al actualizar imagen: " + e.getMessage()));
        }
    }
    // DELETE: Eliminar producto (incluye su imagen)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable Integer id) {
        try {
            productoService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Producto eliminado exitosamente"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    // DELETE: Eliminar solo la imagen del producto
    @DeleteMapping("/{id}/imagen")
    public ResponseEntity<?> deleteImagen(@PathVariable Integer id) {
        try {
            productoService.deleteImagen(id);
            return ResponseEntity.ok(Map.of("message", "Imagen eliminada exitosamente"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    // MODIFICACION STOCK :V
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer nuevoStock = request.get("stock");
            if (nuevoStock == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El campo 'stock' es requerido"));
            }
            ProductoDTO updated = productoService.updateStock(id, nuevoStock);
            return ResponseEntity.ok(updated);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    // GET: Obtener stock de un producto
    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getStock(@PathVariable Integer id) {
        try {
            Integer stock = productoService.getStock(id);
            return ResponseEntity.ok(Map.of("stock", stock));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}