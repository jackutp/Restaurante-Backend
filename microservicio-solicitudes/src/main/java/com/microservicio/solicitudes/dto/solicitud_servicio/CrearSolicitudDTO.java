package com.microservicio.solicitudes.dto.solicitud_servicio;

import com.microservicio.solicitudes.enums.solicitud_servicio.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CrearSolicitudDTO {

    @NotNull(message = "El tipo de solicitud es requerido")
    private TipoSolicitud tipoSolicitud;

    @NotBlank(message = "El título es requerido")
    private String titulo;

    @NotBlank(message = "La descripción es requerida")
    private String descripcion;

    private String prioridad;
    private LocalDate fechaVencimiento;
    private List<String> labels;
    private String assignee;
    private List<SubtareaDTO> subtareas;

    // NUEVOS CAMPOS
    private String usuarioSolicitante;
    private String areaSolicitante;
    private String responsableAsignado;
}