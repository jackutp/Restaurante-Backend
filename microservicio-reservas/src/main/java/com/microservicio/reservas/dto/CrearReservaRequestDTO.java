package com.microservicio.reservas.dto;

import jakarta.validation.constraints.*;

public record CrearReservaRequestDTO(
    @NotNull(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100)
     String nombre,
     String apellido,
    @NotNull(message = "El email es obligatorio")
    @Email(message = "Email inválido")
     String email,
     String telefono,
    @NotNull(message = "La fecha es obligatoria")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Formato de fecha inválido (yyyy-MM-dd)")
     String fecha,  // ← String
    @NotNull(message = "La hora es obligatoria")
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$", message = "Formato de hora inválido")
     String hora,
    @NotNull(message = "El número de personas es obligatorio")
    @Min(1)
    @Max(20)
     Integer personas,
    @NotNull(message = "La experiencia es obligatoria")
     String experiencia,
     String alergias,
     String requerimientos,
     String necesidades
){}

