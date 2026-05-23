package com.microservicio.reservas.repository;

import com.microservicio.reservas.entity.EstadoReserva;
import com.microservicio.reservas.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Optional<Reserva> findByCodigo(String codigo);

    List<Reserva> findByEstado(EstadoReserva estado);

    List<Reserva> findByFecha(LocalDate fecha);

    List<Reserva> findByFechaAndEstado(LocalDate fecha, EstadoReserva estado);
}