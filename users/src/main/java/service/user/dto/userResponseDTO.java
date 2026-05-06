package service.user.dto;

import service.user.model.tipoUser;

public record userResponseDTO(
        Integer idUsuario,
        String nombre,
        String apellido,
        String dni,
        String email,
        tipoUser tipo
) {}