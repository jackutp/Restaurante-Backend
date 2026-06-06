package com.microservicio.solicitudes.repository;

import com.microservicio.solicitudes.entity.Solicitud;
import com.microservicio.solicitudes.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    Optional<Solicitud> findByJiraTicketId(String jiraTicketId);
    List<Solicitud> findByEstado(EstadoSolicitud estado);
    boolean existsByJiraTicketId(String jiraTicketId);
}