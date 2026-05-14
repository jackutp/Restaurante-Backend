package com.microservicio.Proveedor.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorInsumoId implements Serializable {
    private Integer proveedorId;
    private Integer insumoId;
}