package com.microservicio.Proveedor.Mapper;

import com.microservicio.Proveedor.Entities.OrdenCompra;
import com.microservicio.Proveedor.dto.OrdenCompraDTO;
import org.springframework.stereotype.Component;

@Component
public class OrdenCompraMapper {

    public OrdenCompraDTO toDTO(OrdenCompra orden) {
        if (orden == null) return null;

        OrdenCompraDTO dto = new OrdenCompraDTO();
        dto.setOrdenId(orden.getOrdenId());
        dto.setProveedorId(orden.getProveedor().getProveedorid());
        dto.setProveedorNombre(orden.getProveedor().getNombre());
        dto.setFecha(orden.getFecha());
        dto.setEstado(orden.getEstado());
        dto.setFacturaNombre(orden.getFacturaNombre());
        dto.setFacturaTipo(orden.getFacturaTipo());
        dto.setTieneFactura(orden.getFacturaContenido() != null);
        dto.setCreatedAt(orden.getCreatedAt());
        dto.setUpdatedAt(orden.getUpdatedAt());

        return dto;
    }
}