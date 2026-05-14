package com.microservicio.Proveedor.Entities;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "proveedor_insumo")
@IdClass(ProveedorInsumoId.class)
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorInsumo {

    @Id
    @Column(name = "proveedor_id")
    private Integer proveedorId;

    @Id
    @Column(name = "insumo_id")
    private Integer insumoId;
}