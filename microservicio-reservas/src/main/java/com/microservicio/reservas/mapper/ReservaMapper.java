package com.microservicio.reservas.mapper;

import com.microservicio.reservas.dto.CrearReservaRequestDTO;
import com.microservicio.reservas.dto.ReservaResponseDTO;
import com.microservicio.reservas.entity.Reserva;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ReservaMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Reserva toEntity(CrearReservaRequestDTO dto) {
        if (dto == null) return null;

        Reserva reserva = new Reserva();
        reserva.setNombre(dto.getNombre());
        reserva.setApellido(dto.getApellido());
        reserva.setEmail(dto.getEmail());
        reserva.setTelefono(dto.getTelefono());

        // Convertir String a LocalDate
        if (dto.getFecha() != null && !dto.getFecha().isEmpty()) {
            reserva.setFecha(LocalDate.parse(dto.getFecha(), DATE_FORMATTER));
        }

        reserva.setHora(dto.getHora());
        reserva.setPersonas(dto.getPersonas());
        reserva.setExperiencia(dto.getExperiencia());
        reserva.setAlergias(dto.getAlergias());
        reserva.setRequerimientos(dto.getRequerimientos());
        reserva.setNecesidades(dto.getNecesidades());

        return reserva;
    }

    public ReservaResponseDTO toResponseDTO(Reserva reserva) {
        if (reserva == null) return null;

        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(reserva.getId());
        dto.setCodigo(reserva.getCodigo());
        dto.setEstado(reserva.getEstado());
        dto.setNombre(reserva.getNombre());
        dto.setApellido(reserva.getApellido());
        dto.setEmail(reserva.getEmail());
        dto.setTelefono(reserva.getTelefono());

        // Convertir LocalDate a String para la respuesta
        if (reserva.getFecha() != null) {
            dto.setFecha(reserva.getFecha().toString());
        }

        dto.setHora(reserva.getHora());
        dto.setPersonas(reserva.getPersonas());
        dto.setExperiencia(reserva.getExperiencia());
        dto.setAlergias(reserva.getAlergias());
        dto.setRequerimientos(reserva.getRequerimientos());
        dto.setNecesidades(reserva.getNecesidades());
        dto.setCreatedAt(reserva.getCreatedAt());
        dto.setUpdatedAt(reserva.getUpdatedAt());

        return dto;
    }
}