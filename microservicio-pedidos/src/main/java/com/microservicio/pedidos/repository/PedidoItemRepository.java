package com.microservicio.pedidos.repository;

import com.microservicio.pedidos.entity.PedidoItem;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {

    List<PedidoItem> findByPedidoId(Long pedidoId);

    List<PedidoItem> findByPedidoIdAndCompletadoFalse(Long pedidoId);

    //para metrica
    @Query("SELECT pi.nombre, SUM(pi.cantidad), SUM(pi.precio * pi.cantidad) " +
            "FROM PedidoItem pi JOIN pi.pedido p " +
            "WHERE p.createdAt >= :fechaInicio AND p.estado = 'SERVIDO' " +
            "GROUP BY pi.nombre ORDER BY SUM(pi.cantidad) DESC")
    List<Object[]> findTopProductos(@Param("fechaInicio") LocalDateTime fechaInicio, Pageable pageable);
}