
package com.microservicio.Producto.dto;
import com.microservicio.Producto.Entities.Categoria;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class ProductoDTO {
    private Integer productoid;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Categoria categoria;
    private String imagenProducto;
    private String imagenUrl;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

