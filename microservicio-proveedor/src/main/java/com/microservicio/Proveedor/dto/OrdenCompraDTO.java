package com.microservicio.Proveedor.dto;
import com.microservicio.Proveedor.Entities.EstadoOrden;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class OrdenCompraDTO {
    private Integer ordenId;
    private Integer proveedorId;
    private String proveedorNombre;
    private LocalDateTime fecha;
    private EstadoOrden estado;
    private String facturaNombre;
    private String facturaTipo;
    private boolean tieneFactura;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}