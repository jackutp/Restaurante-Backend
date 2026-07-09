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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class OrdenCompraWriteImp implements OrdenCompraWriteService {
    @Autowired
    private OrdenCompraRepository ordenCompraRepository;
    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    private OrdenCompraMapper ordenCompraMapper;

    @Override
    @Transactional
    public OrdenCompraDTO create(OrdenCompraRequestDTO request) {
        log.info("Creando orden de compre para proveedor {}", request.getProveedorId());
        Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

        OrdenCompra orden = new OrdenCompra();
        orden.setProveedor(proveedor);
        orden.setEstado(EstadoOrden.PENDIENTE);

        OrdenCompra saved = ordenCompraRepository.save(orden);
        log.info("Orden de compra {} creada para proveedor {}", saved.getOrdenId(), proveedor.getNombre());
        return ordenCompraMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public OrdenCompraDTO updateEstado(Integer id, EstadoOrden estado) {
        log.info("Actualizando estado de la orden {} a {}", id, estado);
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        orden.setEstado(estado);
        OrdenCompra updated = ordenCompraRepository.save(orden);
        log.info("Orden de compra {} actualizada a estado {}", updated.getOrdenId(), updated.getEstado());
        return ordenCompraMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public OrdenCompraDTO subirFactura(Integer id, MultipartFile archivo) {
        log.info("Subiendo factura {} para la orden {}", archivo.getOriginalFilename(), id);
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        try {
            orden.setFacturaNombre(archivo.getOriginalFilename());
            orden.setFacturaTipo(archivo.getContentType());
            orden.setFacturaContenido(archivo.getBytes());
            orden.setEstado(EstadoOrden.RECIBIDO);  // Al subir factura, cambia a RECIBIDO

            OrdenCompra updated = ordenCompraRepository.save(orden);
            log.info("Factura '{}' asociada correctamente a la orden {}", archivo.getOriginalFilename(), id);
            return ordenCompraMapper.toDTO(updated);
        } catch (IOException e) {
            log.error("Error guardando factura para la orden {}", id);
            throw new FileStorageException("Error al guardar la factura: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void eliminarFactura(Integer id) {
        log.info("Eliminando factura de la orden {}", id);
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        orden.setFacturaNombre(null);
        orden.setFacturaTipo(null);
        orden.setFacturaContenido(null);
        ordenCompraRepository.save(orden);
        log.info("Factura eliminada de la orden {}", id);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando orden de compra {}", id);
        if (!ordenCompraRepository.existsById(id)) {
            log.warn("Intento de eliminar orden inexistente {}", id);
            throw new ResourceNotFoundException("Orden no encontrada");
        }
        ordenCompraRepository.deleteById(id);
        log.warn("Intento de eliminar orden inexistente {}", id);
    }
}
