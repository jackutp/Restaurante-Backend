package com.microservicio.eventos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

@Data
public class EventoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email debe ser válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9\\s-]{8,20}$", message = "Formato de teléfono inválido")
    private String phone;

    private String company;

    @NotNull(message = "La fecha del evento es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser hoy o futura")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "El número de asistentes es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 asistente")
    @Max(value = 500, message = "Máximo 500 asistentes")
    private Integer attendees;

    @NotBlank(message = "Los comentarios son obligatorios")
    @Size(min = 10, max = 1000, message = "Los comentarios deben tener entre 10 y 1000 caracteres")
    private String comments;

    @NotNull(message = "Debes confirmar tu edad")
    @AssertTrue(message = "Debes confirmar que eres mayor de 18 años")
    private Boolean ageConfirmed;

    @NotNull(message = "Debes aceptar las políticas de privacidad")
    @AssertTrue(message = "Debes aceptar las políticas de privacidad")
    private Boolean privacyAccepted;

    private Boolean marketingAccepted = false; // Valor por defecto
}