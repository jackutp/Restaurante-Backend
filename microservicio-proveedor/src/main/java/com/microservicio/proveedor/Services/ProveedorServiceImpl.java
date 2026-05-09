package com.microservicio.proveedor.Services;
import com.microservicio.proveedor.Entities.proveedor;
import com.microservicio.proveedor.Repositories.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
@Service
public class ProveedorServiceImpl implements ProveedorService {
    final private ProveedorRepository proveedorRepository;
    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public List<proveedor> findAll() {
        return (List<proveedor>)proveedorRepository.findAll() ;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<proveedor> findById(Integer id) {
        return proveedorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public proveedor save(proveedor proveedor) {
        // Validar que el RUC no exista
        proveedorRepository.findByRuc(proveedor.getRuc())
                .ifPresent(p -> {
                    throw new IllegalArgumentException(
                            "Ya existe un proveedor con el RUC: " + proveedor.getRuc()
                    );
                });

        proveedor.setProveedorid(null); // Asegurar que sea una nueva entidad
        return proveedorRepository.save(proveedor);
    }

    @Override
    @Transactional
    public proveedor update(Integer id, proveedor proveedor) {
        return proveedorRepository.findById(id)
                .map(proveedorExistente -> {
                    proveedorExistente.setNombre(proveedor.getNombre());
                    proveedorExistente.setDescripcion(proveedor.getDescripcion());
                    proveedorExistente.setRuc(proveedor.getRuc());
                    proveedorExistente.setRazonSocial(proveedor.getRazonSocial());
                    proveedorExistente.setDireccionFiscal(proveedor.getDireccionFiscal());
                    return proveedorRepository.save(proveedorExistente);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        "Proveedor no encontrado con ID: " + id
                ));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!proveedorRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Proveedor no encontrado con ID: " + id
            );
        }
        proveedorRepository.deleteById(id);
    }
}