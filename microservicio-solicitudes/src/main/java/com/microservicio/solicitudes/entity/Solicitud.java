package com.microservicio.solicitudes.entity;

import com.microservicio.solicitudes.enums.EstadoSolicitud;
import com.microservicio.solicitudes.enums.TipoSolicitud;
import com.microservicio.solicitudes.enums.Prioridad;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigoTicket;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSolicitud tipoSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Enumerated(EnumType.STRING)
    private Prioridad prioridad = Prioridad.MEDIA;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Column(name = "sla_fecha_limite")
    private LocalDateTime slaFechaLimite;

    @Column(name = "usuario_solicitante")
    private String usuarioSolicitante;

    @Column(name = "area_solicitante")
    private String areaSolicitante;

    @Column(name = "responsable_asignado")
    private String responsableAsignado;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(length = 1000)
    private String resolucion;

    @Column(name = "jira_ticket_id", unique = true)
    private String jiraTicketId;

    @Column(name = "jira_url")
    private String jiraUrl;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        this.codigoTicket = generarCodigoTicket();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    private String generarCodigoTicket() {
        return "TKT-" + System.currentTimeMillis();
    }
}