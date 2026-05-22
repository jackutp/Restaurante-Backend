package com.microservicio.mesas.repository;
import com.microservicio.mesas.entity.EstadoMesa;
import com.microservicio.mesas.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    Optional<Mesa> findByNumero(Integer numero);
    List<Mesa> findByEstado(EstadoMesa estado);
    boolean existsByNumero(Integer numero);
    List<Mesa> findByEstadoOrderByNumeroAsc(EstadoMesa estado);
}