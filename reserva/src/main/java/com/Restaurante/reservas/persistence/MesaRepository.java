package com.Restaurante.reservas.persistence;

import com.Restaurante.reservas.entities.Mesa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepository extends CrudRepository<Mesa, Long> {
}
