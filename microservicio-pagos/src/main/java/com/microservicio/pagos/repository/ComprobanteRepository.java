package com.microservicio.pagos.repository;
import com.microservicio.pagos.entity.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    @Query("SELECT MAX(c.correlativo) FROM Comprobante c WHERE c.serie = :serie")
    Integer findMaxCorrelativoBySerie(String serie);
    Comprobante findByNumeroCompleto(String numeroCompleto);
    List<Comprobante> findAllByOrderByIdDesc();
    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM comprobantes WHERE created_at >= CAST(?1 AS TIMESTAMP) AND created_at <= CAST(?2 AS TIMESTAMP)", nativeQuery = true)
    Double sumTotalByDateRangeNative(String inicio, String fin);
    //Ventas por día
    @Query(value = "SELECT DATE(created_at) as fecha, COALESCE(SUM(total), 0) as total FROM comprobantes " +
            "WHERE created_at >= CAST(?1 AS TIMESTAMP) AND created_at <= CAST(?2 AS TIMESTAMP) " +
            "GROUP BY DATE(created_at) ORDER BY fecha ASC", nativeQuery = true)
    List<Object[]> sumTotalByDayNative(String inicio, String fin);
    //Ventas por hora
    @Query(value = "SELECT EXTRACT(HOUR FROM created_at) as hora, COALESCE(SUM(total), 0) as total FROM comprobantes " +
            "WHERE created_at >= CAST(?1 AS TIMESTAMP) AND created_at <= CAST(?2 AS TIMESTAMP) " +
            "GROUP BY EXTRACT(HOUR FROM created_at) ORDER BY hora ASC", nativeQuery = true)
    List<Object[]> sumTotalByHourNative(String inicio, String fin);
}