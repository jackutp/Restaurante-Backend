package com.microservicio.Mermas.dto;

import lombok.Data;

@Data
public class InsumoDTO {
    private Integer insumoid;
    private String nombre;
    private String unidadMedida;
    private Integer stock;
    private String estadoInsumo;
}