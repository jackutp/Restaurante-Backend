package com.microservicio.Proveedor.Services.orden_compra;

import com.microservicio.Proveedor.Entities.EstadoOrden;
import com.microservicio.Proveedor.dto.OrdenCompraDTO;
import com.microservicio.Proveedor.dto.OrdenCompraRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface OrdenCompraWriteService {
    OrdenCompraDTO create(OrdenCompraRequestDTO request);
    OrdenCompraDTO updateEstado(Integer id, EstadoOrden estado);
    OrdenCompraDTO subirFactura(Integer id, MultipartFile archivo);
    void eliminarFactura(Integer id);
    void delete(Integer id);
}
