package service.user.dto;

import service.user.model.TipoUser;

public record UserResponseDTO(
        Integer idUsuario,
        String nombre,
        String apellido,
        String dni,
        String email,
        TipoUser tipo,
        String token
) {}