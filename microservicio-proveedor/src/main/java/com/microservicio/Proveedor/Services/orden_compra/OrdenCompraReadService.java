package com.microservicio.Proveedor.Services.orden_compra;

import com.microservicio.Proveedor.dto.OrdenCompraDTO;

import java.util.List;
import java.util.Optional;

public interface OrdenCompraReadService {
    List<OrdenCompraDTO> findAll();
    List<OrdenCompraDTO> findByProveedor(Integer proveedorId);
    OrdenCompraDTO findById(Integer id);
    byte[] descargarFactura(Integer id);
}
