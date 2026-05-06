package com.Restaurante.reservas.persistence;

import com.Restaurante.reservas.entities.Mesa;
import com.Restaurante.reservas.entities.Reserva;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends CrudRepository<Reserva, Long> {
}
