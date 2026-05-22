package com.microservicio.Proveedor.Services.orden_compra;

import com.microservicio.Proveedor.Entities.EstadoOrden;
import com.microservicio.Proveedor.Entities.OrdenCompra;
import com.microservicio.Proveedor.Entities.Proveedor;
import com.microservicio.Proveedor.Mapper.OrdenCompraMapper;
import com.microservicio.Proveedor.Repositories.OrdenCompraRepository;
import com.microservicio.Proveedor.Repositories.ProveedorRepository;
import com.microservicio.Proveedor.dto.OrdenCompraDTO;
import com.microservicio.Proveedor.dto.OrdenCompraRequestDTO;
import com.microservicio.Proveedor.exception.FileStorageException;
import com.microservicio.Proveedor.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class OrdenCompraWriteImp implements OrdenCompraWriteService {
    @Autowired
    private  OrdenCompraRepository ordenCompraRepository;
    @Autowired
    private  ProveedorRepository proveedorRepository;
    @Autowired
    private  OrdenCompraMapper ordenCompraMapper;

    @Override
    @Transactional
    public OrdenCompraDTO create(OrdenCompraRequestDTO request) {
        Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

        OrdenCompra orden = new OrdenCompra();
        orden.setProveedor(proveedor);
        orden.setEstado(EstadoOrden.PENDIENTE);

        OrdenCompra saved = ordenCompraRepository.save(orden);
        return ordenCompraMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public OrdenCompraDTO updateEstado(Integer id, EstadoOrden estado) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        orden.setEstado(estado);
        OrdenCompra updated = ordenCompraRepository.save(orden);
        return ordenCompraMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public OrdenCompraDTO subirFactura(Integer id, MultipartFile archivo) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        try {
            orden.setFacturaNombre(archivo.getOriginalFilename());
            orden.setFacturaTipo(archivo.getContentType());
            orden.setFacturaContenido(archivo.getBytes());
            orden.setEstado(EstadoOrden.RECIBIDO);  // Al subir factura, cambia a RECIBIDO

            OrdenCompra updated = ordenCompraRepository.save(orden);
            return ordenCompraMapper.toDTO(updated);
        } catch (IOException e) {
            throw new FileStorageException("Error al guardar la factura: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void eliminarFactura(Integer id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        orden.setFacturaNombre(null);
        orden.setFacturaTipo(null);
        orden.setFacturaContenido(null);
        ordenCompraRepository.save(orden);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!ordenCompraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Orden no encontrada");
        }
        ordenCompraRepository.deleteById(id);
    }
}
