package com.microservicio.eventos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventoStatusUpdateDTO {
    @NotNull(message = "El estado es obligatorio")
    private String status;
    private String reason;
}