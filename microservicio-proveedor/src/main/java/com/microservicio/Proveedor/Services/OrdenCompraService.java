package com.microservicio.Proveedor.Services;
import com.microservicio.Proveedor.Entities.EstadoOrden;
import com.microservicio.Proveedor.dto.OrdenCompraDTO;
import com.microservicio.Proveedor.dto.OrdenCompraRequestDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;
public interface OrdenCompraService {
    List<OrdenCompraDTO> findAll();
    List<OrdenCompraDTO> findByProveedor(Integer proveedorId);
    Optional<OrdenCompraDTO> findById(Integer id);
    OrdenCompraDTO create(OrdenCompraRequestDTO request);
    OrdenCompraDTO updateEstado(Integer id, EstadoOrden estado);
    OrdenCompraDTO subirFactura(Integer id, MultipartFile archivo);
    byte[] descargarFactura(Integer id);
    void eliminarFactura(Integer id);
    void delete(Integer id);
}