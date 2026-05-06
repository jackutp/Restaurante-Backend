package com.microservicio.inventario.dto;

import java.time.LocalDate;

public record MermaDTO(
        Long mermaInsumoId,
        String motivo,
        LocalDate fecha,
        Long ingredienteId  // Solo ID para evitar circularidad
) {}