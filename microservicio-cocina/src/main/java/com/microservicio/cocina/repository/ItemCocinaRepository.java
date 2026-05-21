package com.microservicio.cocina.repository;

import com.microservicio.cocina.entity.ItemCocina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCocinaRepository extends JpaRepository<ItemCocina, Long> {
}