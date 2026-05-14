package com.microservicio.Proveedor.Repositories;


import com.microservicio.Proveedor.Entities.ProveedorInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProveedorInsumoRepository extends JpaRepository<ProveedorInsumo, Integer> {

    @Query("SELECT pi.insumoId FROM ProveedorInsumo pi WHERE pi.proveedorId = :proveedorId")
    List<Integer> findInsumoIdsByProveedorId(@Param("proveedorId") Integer proveedorId);
}