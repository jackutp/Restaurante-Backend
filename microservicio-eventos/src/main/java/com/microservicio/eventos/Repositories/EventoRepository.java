package com.microservicio.eventos.Repositories;

import com.microservicio.eventos.Entities.EventoRequest;
import com.microservicio.eventos.Entities.EventoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<EventoRequest, Long> {
    // Buscar por estado
    Page<EventoRequest> findByStatus(EventoStatus status, Pageable pageable);
    // Buscar por rango de fechas
    List<EventoRequest> findByDateBetween(LocalDate startDate, LocalDate endDate);
    // Contar por estado
    @Query("SELECT e.status, COUNT(e) FROM EventoRequest e GROUP BY e.status")
    List<Object[]> countByStatus();
    // Buscar eventos por email
    List<EventoRequest> findByEmail(String email);
    // Verificar disponibilidad en fecha específica
    @Query("SELECT COUNT(e) FROM EventoRequest e WHERE e.date = :date")
    Long countByDate(@Param("date") LocalDate date);
    // Eventos del día actual
    List<EventoRequest> findByDate(LocalDate date);
    // Filtro dinámico con múltiples parámetros
    @Query("SELECT e FROM EventoRequest e WHERE " +
            "(:status IS NULL OR e.status = :status) AND " +
            "(:startDate IS NULL OR e.date >= :startDate) AND " +
            "(:endDate IS NULL OR e.date <= :endDate)")
    Page<EventoRequest> findByFilters(@Param("status") EventoStatus status,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      Pageable pageable);
}