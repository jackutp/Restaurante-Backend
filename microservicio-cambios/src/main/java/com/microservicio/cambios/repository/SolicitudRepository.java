package com.microservicio.cambios.repository;

import com.microservicio.cambios.entity.solicitud_servicio.Solicitud;
import com.microservicio.cambios.enums.EstadoCambio;
import com.microservicio.cambios.enums.solicitud_servicio.TipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    Optional<Solicitud> findByJiraTicketId(String jiraTicketId);

    Optional<Solicitud> findByCodigoTicket(String codigoTicket);

    List<Solicitud> findByEstado(EstadoCambio estado);

    List<Solicitud> findByTipoSolicitud(TipoSolicitud tipoSolicitud);

    List<Solicitud> findByTipoSolicitudAndEstado(TipoSolicitud tipoSolicitud, EstadoCambio estado);

    boolean existsByJiraTicketId(String jiraTicketId);

    long countByEstado(EstadoCambio estado);

    long countByTipoSolicitud(TipoSolicitud tipoSolicitud);
}