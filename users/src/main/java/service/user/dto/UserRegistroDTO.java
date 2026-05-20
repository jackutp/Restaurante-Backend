package service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistroDTO(
        @NotBlank(message = "Nombre obligatorio")
        @Size(min=2, max=100)
        String nombre,
        @NotBlank(message = "Apellido obligatorio")
        @Size(min=2, max=100)
        String apellido,
        @NotBlank(message = "DNI obligatorio")
        @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener 8 dígitos")
        String dni,
        @NotBlank(message = "Email obligatorio")
        @Email(message = "Email inválido")
        String email,
        @NotBlank(message = "Clave obligatoria")
        @Size(min=8, max=100, message = "La clave debe ser de al menos 8 caracteres")
        String clave
) {}