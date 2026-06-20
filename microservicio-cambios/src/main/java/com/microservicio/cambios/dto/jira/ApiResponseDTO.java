package com.microservicio.cambios.dto.jira;

public record ApiResponseDTO(
        boolean success,
        String message,
        Object data
) {
}