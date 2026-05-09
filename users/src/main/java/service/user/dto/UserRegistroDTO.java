package service.user.dto;

public record UserRegistroDTO(
        String nombre,
        String apellido,
        String dni,
        String email,
        String clave
) {}