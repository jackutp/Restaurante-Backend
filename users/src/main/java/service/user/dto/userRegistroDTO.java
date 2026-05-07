package service.user.dto;

public record userRegistroDTO(
        String nombre,
        String apellido,
        String dni,
        String email,
        String clave
) {}