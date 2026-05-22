package com.microservicio.Proveedor.dto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraRequestDTO {
    @NotNull(message = "El proveedor es obligatorio")
    private Integer proveedorId;

    @NotNull(message = "Debe seleccionar al menos un insumo")
    private List<Integer> insumosIds;

}