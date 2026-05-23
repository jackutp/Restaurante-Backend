package com.microservicio.Proveedor.dto;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class ProveedorDTO {
    private Integer proveedorid;
    private String nombre;
    private String descripcion;
    private String ruc;
    private String razonSocial;
    private String direccionFiscal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}