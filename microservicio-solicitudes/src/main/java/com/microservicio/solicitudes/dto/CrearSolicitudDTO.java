package com.microservicio.solicitudes.dto;

import com.microservicio.solicitudes.enums.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CrearSolicitudDTO {

    @NotNull(message = "El tipo de solicitud es requerido")
    private TipoSolicitud tipoSolicitud;  // SERVICIO, INFORMACION, ACCESO

    @NotBlank(message = "El título es requerido")
    private String titulo;

    @NotBlank(message = "La descripción es requerida")
    private String descripcion;

    private String prioridad;  // Highest, High, Medium, Low, Lowest
    private LocalDate fechaVencimiento;
    private List<String> labels;
    private String assignee;
    private List<SubtareaDTO> subtareas;
}