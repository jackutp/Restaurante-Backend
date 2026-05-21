package com.microservicio.pagos.repository;

import com.microservicio.pagos.entity.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    @Query("SELECT MAX(c.correlativo) FROM Comprobante c WHERE c.serie = :serie")
    Integer findMaxCorrelativoBySerie(@Param("serie") String serie);

    Comprobante findByNumeroCompleto(String numeroCompleto);
    List<Comprobante> findAllByOrderByIdDesc();

}