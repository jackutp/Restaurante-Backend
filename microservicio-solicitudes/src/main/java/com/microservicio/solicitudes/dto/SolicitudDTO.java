package com.microservicio.solicitudes.dto;

import com.microservicio.solicitudes.enums.EstadoSolicitud;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDTO {
    private Long id;
    private String titulo;
    private String descripcion;  // ← Asegura que este campo existe
    private EstadoSolicitud estado;
    private String jiraTicketId;
    private String jiraUrl;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}