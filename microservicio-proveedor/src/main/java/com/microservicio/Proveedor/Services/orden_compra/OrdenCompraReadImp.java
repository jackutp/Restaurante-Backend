package com.microservicio.Proveedor.Services.orden_compra;

import com.microservicio.Proveedor.Entities.OrdenCompra;
import com.microservicio.Proveedor.Entities.Proveedor;
import com.microservicio.Proveedor.Mapper.OrdenCompraMapper;
import com.microservicio.Proveedor.Repositories.OrdenCompraRepository;
import com.microservicio.Proveedor.Repositories.ProveedorRepository;
import com.microservicio.Proveedor.dto.OrdenCompraDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrdenCompraReadImp implements OrdenCompraReadService {
    private final OrdenCompraRepository ordenCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final OrdenCompraMapper ordenCompraMapper;

    public OrdenCompraReadImp(OrdenCompraRepository ordenCompraRepository,
                                  ProveedorRepository proveedorRepository,
                                  OrdenCompraMapper ordenCompraMapper) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.proveedorRepository = proveedorRepository;
        this.ordenCompraMapper = ordenCompraMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> findAll() {
        return ordenCompraRepository.findAll()
                .stream()
                .map(ordenCompraMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> findByProveedor(Integer proveedorId) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));
        return ordenCompraRepository.findByProveedorOrderByFechaDesc(proveedor)
                .stream()
                .map(ordenCompraMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdenCompraDTO> findById(Integer id) {
        return ordenCompraRepository.findById(id)
                .map(ordenCompraMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] descargarFactura(Integer id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        if (orden.getFacturaContenido() == null) {
            throw new RuntimeException("La orden no tiene factura asociada");
        }
        return orden.getFacturaContenido();
    }
}
