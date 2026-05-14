package com.microservicio.Proveedor.Repositories;


import com.microservicio.Proveedor.Entities.OrdenCompra;
import com.microservicio.Proveedor.Entities.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Integer> {
    List<OrdenCompra> findByProveedorOrderByFechaDesc(Proveedor proveedor);
    List<OrdenCompra> findByEstado(String estado);
}