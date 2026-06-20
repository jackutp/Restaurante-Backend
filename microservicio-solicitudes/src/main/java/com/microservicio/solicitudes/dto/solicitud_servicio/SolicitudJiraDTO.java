package com.microservicio.solicitudes.dto.solicitud_servicio;

import com.microservicio.solicitudes.enums.TipoSolicitud;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SolicitudJiraDTO {
    private TipoSolicitud tipoSolicitud;
    private String titulo;
    private String descripcion;
    private String prioridad;  // Highest, High, Medium, Low, Lowest
    private LocalDate fechaVencimiento;
    private List<String> labels;
    private String assignee;
    private List<SubtareaDTO> subtareas;
}