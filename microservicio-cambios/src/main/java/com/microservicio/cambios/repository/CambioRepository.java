package com.microservicio.cambios.repository;

import com.microservicio.cambios.entity.Cambio;
import com.microservicio.cambios.enums.EstadoCambio;
import com.microservicio.cambios.enums.TipoCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CambioRepository extends JpaRepository<Cambio, Long> {
    Optional<Cambio> findByJiraTicketId(String jiraTicketId);

    Optional<Cambio> findByCodigoTicket(String codigoTicket);

    List<Cambio> findByEstado(EstadoCambio estado);

    List<Cambio> findByTipoCambio(TipoCambio tipoSolicitud);

    List<Cambio> findByTipoCambioAndEstado(TipoCambio tipoSolicitud, EstadoCambio estado);

    boolean existsByJiraTicketId(String jiraTicketId);

    long countByEstado(EstadoCambio estado);

    long countByTipoCambio(TipoCambio tipoSolicitud);
}