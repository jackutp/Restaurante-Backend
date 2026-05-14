package com.microservicio.Producto.Repositories;

import com.microservicio.Producto.Entities.Producto;
import com.microservicio.Producto.Entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    // Buscar por nombre
    Optional<Producto> findByNombre(String nombre);
    // Buscar por categoría
    List<Producto> findByCategoria(Categoria categoria);
    // Buscar productos que tengan imagen
    @Query("SELECT p FROM Producto p WHERE p.imagenProducto IS NOT NULL")
    List<Producto> findAllWithImage();
    // Buscar por rango de precio
    List<Producto> findByPrecioBetween(BigDecimal min, BigDecimal max);
    // Actualizar solo la imagen (optimizado)
    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.imagenProducto = :imagenPath WHERE p.productoid = :id")
    int updateImagenPath(@Param("id") Integer id, @Param("imagenPath") String imagenPath);
}