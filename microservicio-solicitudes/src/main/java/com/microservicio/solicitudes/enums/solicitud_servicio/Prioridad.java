package com.microservicio.solicitudes.enums.solicitud_servicio;

public enum Prioridad {
    ALTA("Alta", "Requiere atención inmediata", 1),
    MEDIA("Media", "Requiere atención dentro del horario laboral", 2),
    BAJA("Baja", "Puede esperar", 3);

    private final String nombre;
    private final String descripcion;
    private final int nivel;

    Prioridad(String nombre, String descripcion, int nivel) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nivel = nivel;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getNivel() { return nivel; }
}