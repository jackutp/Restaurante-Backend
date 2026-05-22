package com.microservicio.Proveedor.Services;
import com.microservicio.Proveedor.Entities.EstadoOrden;
import com.microservicio.Proveedor.Entities.OrdenCompra;
import com.microservicio.Proveedor.Entities.Proveedor;
import com.microservicio.Proveedor.Repositories.OrdenCompraRepository;
import com.microservicio.Proveedor.Repositories.ProveedorRepository;
import com.microservicio.Proveedor.dto.OrdenCompraDTO;
import com.microservicio.Proveedor.dto.OrdenCompraRequestDTO;
import com.microservicio.Proveedor.Mapper.OrdenCompraMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
@Service
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final OrdenCompraMapper ordenCompraMapper;

    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository,
                                  ProveedorRepository proveedorRepository,
                                  OrdenCompraMapper ordenCompraMapper) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.proveedorRepository = proveedorRepository;
        this.ordenCompraMapper = ordenCompraMapper;
    }
    @Override
    public List<OrdenCompraDTO> findAll() {
        return ordenCompraRepository.findAll()
                .stream()
                .map(ordenCompraMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdenCompraDTO> findByProveedor(Integer proveedorId) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));
        return ordenCompraRepository.findByProveedorOrderByFechaDesc(proveedor)
                .stream()
                .map(ordenCompraMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrdenCompraDTO> findById(Integer id) {
        return ordenCompraRepository.findById(id)
                .map(ordenCompraMapper::toDTO);
    }

    @Override
    @Transactional
    public OrdenCompraDTO create(OrdenCompraRequestDTO request) {
        Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

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
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        orden.setEstado(estado);
        OrdenCompra updated = ordenCompraRepository.save(orden);
        return ordenCompraMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public OrdenCompraDTO subirFactura(Integer id, MultipartFile archivo) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        try {
            orden.setFacturaNombre(archivo.getOriginalFilename());
            orden.setFacturaTipo(archivo.getContentType());
            orden.setFacturaContenido(archivo.getBytes());
            orden.setEstado(EstadoOrden.RECIBIDO);

            OrdenCompra updated = ordenCompraRepository.save(orden);
            return ordenCompraMapper.toDTO(updated);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la factura: " + e.getMessage());
        }
    }

    @Override
    public byte[] descargarFactura(Integer id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        if (orden.getFacturaContenido() == null) {
            throw new RuntimeException("La orden no tiene factura asociada");
        }
        return orden.getFacturaContenido();
    }

    @Override
    @Transactional
    public void eliminarFactura(Integer id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"));

        orden.setFacturaNombre(null);
        orden.setFacturaTipo(null);
        orden.setFacturaContenido(null);
        ordenCompraRepository.save(orden);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!ordenCompraRepository.existsById(id)) {
            throw new EntityNotFoundException("Orden no encontrada");
        }
        ordenCompraRepository.deleteById(id);
    }
}