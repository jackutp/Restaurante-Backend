package com.microservicio.proveedor.Entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
@Setter
@Getter
@Entity
@Table(name = "proveedores")
public class proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proveedor_id")
    private Integer proveedorid;
    private String nombre;
    private String descripcion;
    @Column(unique = true, length = 11)
    @Pattern(regexp = "^[0-9]{11}$")
    private String ruc;
    private String razonSocial;
    private  String direccionFiscal;
    public proveedor(Integer proveedorid, String nombre, String descripcion, String ruc, String razonSocial, String direccionFiscal) {
        this.proveedorid = proveedorid;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.direccionFiscal = direccionFiscal;
    }
    public proveedor() {
    }
}

