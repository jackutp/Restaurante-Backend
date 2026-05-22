package com.microservicio.Proveedor.Services.orden_compra;

import com.microservicio.Proveedor.Entities.OrdenCompra;
import com.microservicio.Proveedor.Entities.Proveedor;
import com.microservicio.Proveedor.Mapper.OrdenCompraMapper;
import com.microservicio.Proveedor.Repositories.OrdenCompraRepository;
import com.microservicio.Proveedor.Repositories.ProveedorRepository;
import com.microservicio.Proveedor.dto.OrdenCompraDTO;
import com.microservicio.Proveedor.exception.FileStorageException;
import com.microservicio.Proveedor.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrdenCompraReadImp implements OrdenCompraReadService {
    @Autowired
    private  OrdenCompraRepository ordenCompraRepository;
    @Autowired
    private  ProveedorRepository proveedorRepository;
    @Autowired
    private  OrdenCompraMapper ordenCompraMapper;

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
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
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
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        if (orden.getFacturaContenido() == null) {
            throw new FileStorageException("La orden no tiene factura asociada");
        }
        return orden.getFacturaContenido();
    }
}
