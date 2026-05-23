package com.microservicio.pagos.repository;
import com.microservicio.pagos.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByOrdenId(String ordenId);
    List<Pago> findByMesaNumero(Integer mesaNumero);
}