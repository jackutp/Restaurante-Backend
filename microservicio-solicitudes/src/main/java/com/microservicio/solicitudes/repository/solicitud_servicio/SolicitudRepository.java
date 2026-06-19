package com.microservicio.solicitudes.repository.solicitud_servicio;

import com.microservicio.solicitudes.entity.solicitud_servicio.Solicitud;
import com.microservicio.solicitudes.enums.jira.EstadoSolicitud;
import com.microservicio.solicitudes.enums.solicitud_servicio.TipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    Optional<Solicitud> findByJiraTicketId(String jiraTicketId);
    Optional<Solicitud> findByCodigoTicket(String codigoTicket);
    List<Solicitud> findByEstado(EstadoSolicitud estado);
    List<Solicitud> findByTipoSolicitud(TipoSolicitud tipoSolicitud);
    List<Solicitud> findByTipoSolicitudAndEstado(TipoSolicitud tipoSolicitud, EstadoSolicitud estado);
    boolean existsByJiraTicketId(String jiraTicketId);
    long countByEstado(EstadoSolicitud estado);
    long countByTipoSolicitud(TipoSolicitud tipoSolicitud);
}