package com.microservicio.solicitudes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubtareaDTO {
    private String titulo;
    private String descripcion;
    private String prioridad;  // Highest, High, Medium, Low, Lowest
}