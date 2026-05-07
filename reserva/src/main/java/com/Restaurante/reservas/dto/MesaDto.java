package com.Restaurante.reservas.dto;

import com.Restaurante.reservas.entities.Mesa;

public record MesaDto(Long mesa_id, int numero, int capacidad, boolean ocupado, Mesa.mesa_tipo tipo) {
}
