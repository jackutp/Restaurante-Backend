package com.microservicio.cambios.dto.solicitud_servicio;

import com.microservicio.cambios.enums.EstadoSolicitud;
import com.microservicio.cambios.enums.solicitud_servicio.TipoSolicitud;
import com.microservicio.cambios.enums.solicitud_servicio.Prioridad;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDTO {
    private Long id;
    private String codigoTicket;
    private String titulo;
    private String descripcion;
    private TipoSolicitud tipoSolicitud;
    private EstadoSolicitud estado;
    private Prioridad prioridad;
    private LocalDateTime fechaVencimiento;
    private LocalDateTime slaFechaLimite;
    private String usuarioSolicitante;
    private String areaSolicitante;
    private String responsableAsignado;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaResolucion;
    private String resolucion;
    private String jiraTicketId;
    private String jiraUrl;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Campos adicionales para Jira
    private List<String> labels;
    private List<SubtareaDTO> subtareas;
}