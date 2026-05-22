package com.microservicio.Insumos.Repositories;
import com.microservicio.Insumos.Entities.Insumos;
import com.microservicio.Insumos.Entities.EstadoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
@Repository
public interface InsumoRepository extends JpaRepository<Insumos, Integer> {
    Optional<Insumos> findByNombre(String nombre);
    // Búsqueda por nombre parcial con LIKE
    @Query("SELECT i FROM Insumos i WHERE LOWER(i.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Insumos> searchByNombre(@Param("nombre") String nombre);
    List<Insumos> findByEstadoInsumo(EstadoInsumo estado);
    @Query("SELECT i FROM Insumos i WHERE i.stock < 10 AND i.stock > 0")
    List<Insumos> findLowStock();
    @Query("SELECT i FROM Insumos i WHERE i.stock = 0")
    List<Insumos> findOutOfStock();
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Insumos i SET i.stock = :stock, i.estadoInsumo = CASE WHEN :stock <= 0 THEN 'VACIO' WHEN :stock < 10 THEN 'BAJO' ELSE 'DISPONIBLE' END WHERE i.insumoid = :id")
    int updateStock(@Param("id") Integer id, @Param("stock") Integer stock);
}