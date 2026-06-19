package com.microservicio.cambios.entity;

import com.microservicio.cambios.enums.CategoriaCambio;
import com.microservicio.cambios.enums.EstadoCambio;
import com.microservicio.cambios.enums.RiesgoCambio;
import com.microservicio.cambios.enums.TipoCambio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cambios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cambio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigoTicket;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCambio tipoCambio = TipoCambio.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaCambio categoriaCambio = CategoriaCambio.INFRAESTRUCTURA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCambio estado = EstadoCambio.PENDIENTE;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Column(name = "usuario_solicitante")
    private String usuarioSolicitante;

    @Column(name = "area_solicitante")
    private String areaSolicitante;

    @Column(name = "responsable_asignado")
    private String responsableAsignado;

    @Column(name = "jira_ticket_id", unique = true)
    private String jiraTicketId;

    @Column(name = "jira_url")
    private String jiraUrl;

    @Column(name = "sistema_afectado")
    private String sistemaAfectado; //docker, microservicio-productos, postgres, github-actions, etc

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiesgoCambio riesgo = RiesgoCambio.MEDIO;

    @Column(name = "aprobado_por")
    private String aprobadoPor;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "fecha_implementacion")
    private LocalDateTime fechaImplementacion;

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