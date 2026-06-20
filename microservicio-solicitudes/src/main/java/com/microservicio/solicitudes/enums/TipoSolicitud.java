package com.microservicio.solicitudes.enums;

public enum TipoSolicitud {
    SERVICIO("Servicio", "Soporte técnico, atención, configuración"),
    INFORMACION("Información", "Datos, reportes, estado de servicios"),
    ACCESO("Acceso", "Permisos, usuarios, roles");

    private final String nombre;
    private final String descripcion;

    TipoSolicitud(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}