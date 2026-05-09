package com.microservicio.proveedor.Services;
import com.microservicio.proveedor.Entities.proveedor;
import java.util.List;
import java.util.Optional;
public interface ProveedorService {
    List<proveedor>findAll();
    Optional<proveedor>findById(Integer id);
    proveedor save(proveedor proveedor);
    proveedor update(Integer id, proveedor proveedor);
    void delete(Integer id);
}