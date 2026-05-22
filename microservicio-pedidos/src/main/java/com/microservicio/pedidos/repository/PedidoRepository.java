package com.microservicio.pedidos.repository;

import com.microservicio.pedidos.entity.EstadoPedido;
import com.microservicio.pedidos.entity.Pedido;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByOrdenId(String ordenId);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByMesaNumero(Integer mesaNumero);

    List<Pedido> findByEstadoOrderByCreatedAtDesc(EstadoPedido estado);

    //para metricas
    // Contar órdenes por estado
    @Query("SELECT p.estado, COUNT(p) FROM Pedido p GROUP BY p.estado")
    List<Object[]> countByEstado();

    // Contar órdenes completadas hoy
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = 'SERVIDO' AND p.createdAt >= CURRENT_DATE")
    Long countOrdenesCompletadasHoy();

    // Productos más vendidos (últimos 7 días)
    @Query("SELECT pi.nombre, SUM(pi.cantidad), SUM(pi.precio * pi.cantidad) " +
            "FROM PedidoItem pi JOIN pi.pedido p " +
            "WHERE p.createdAt >= :fechaInicio AND p.estado = 'SERVIDO' " +
            "GROUP BY pi.nombre ORDER BY SUM(pi.cantidad) DESC")
    List<Object[]> findTopProductos(@Param("fechaInicio") LocalDateTime fechaInicio, Pageable pageable);
}