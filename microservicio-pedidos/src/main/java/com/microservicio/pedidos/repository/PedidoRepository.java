package com.microservicio.pedidos.repository;

import com.microservicio.pedidos.entity.EstadoPedido;
import com.microservicio.pedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByOrdenId(String ordenId);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByMesaNumero(Integer mesaNumero);

    List<Pedido> findByEstadoOrderByCreatedAtDesc(EstadoPedido estado);
}