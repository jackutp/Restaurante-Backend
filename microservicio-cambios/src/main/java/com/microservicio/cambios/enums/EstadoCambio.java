package com.microservicio.cambios.enums;

public enum EstadoCambio {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En Proceso"),
    COMPLETADA("Completada"),
    RECHAZADA("Rechazada");

    private final String descripcion;

    EstadoCambio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}