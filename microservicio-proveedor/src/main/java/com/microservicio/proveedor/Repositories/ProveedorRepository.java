package com.microservicio.proveedor.Repositories;
import com.microservicio.proveedor.Entities.proveedor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProveedorRepository extends CrudRepository <proveedor, Integer>{
    Optional<proveedor> findByRuc(String ruc);
    boolean existsByRuc(String ruc);


}
