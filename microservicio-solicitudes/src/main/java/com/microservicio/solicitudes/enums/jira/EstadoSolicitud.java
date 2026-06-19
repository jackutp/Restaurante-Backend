package com.microservicio.solicitudes.enums.jira;

public enum EstadoSolicitud {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En Proceso"),
    COMPLETADA("Completada"),
    RECHAZADA("Rechazada");

    private final String descripcion;

    EstadoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() { return descripcion; }
}