package com.microservicio.reservas.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class CrearReservaRequestDTO {

    @NotNull(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100)
    private String nombre;

    private String apellido;

    @NotNull(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    private String telefono;

    @NotNull(message = "La fecha es obligatoria")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Formato de fecha inválido (yyyy-MM-dd)")
    private String fecha;  // ← String

    @NotNull(message = "La hora es obligatoria")
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$", message = "Formato de hora inválido")
    private String hora;

    @NotNull(message = "El número de personas es obligatorio")
    @Min(1)
    @Max(20)
    private Integer personas;

    @NotNull(message = "La experiencia es obligatoria")
    private String experiencia;

    private String alergias;
    private String requerimientos;
    private String necesidades;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public Integer getPersonas() { return personas; }
    public void setPersonas(Integer personas) { this.personas = personas; }

    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public String getRequerimientos() { return requerimientos; }
    public void setRequerimientos(String requerimientos) { this.requerimientos = requerimientos; }

    public String getNecesidades() { return necesidades; }
    public void setNecesidades(String necesidades) { this.necesidades = necesidades; }
}