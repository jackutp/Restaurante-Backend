package com.microservicio.cocina.repository;

import com.microservicio.cocina.entity.PedidoCocina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoCocinaRepository extends JpaRepository<PedidoCocina, Long> {

    Optional<PedidoCocina> findByOrdenId(String ordenId);

    List<PedidoCocina> findByEstado(String estado);

    List<PedidoCocina> findByEstadoIn(List<String> estados);
}