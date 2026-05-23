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
        reserva.setNombre(dto.nombre());
        reserva.setApellido(dto.apellido());
        reserva.setEmail(dto.email());
        reserva.setTelefono(dto.telefono());

        // Convertir String a LocalDate
        if (dto.fecha() != null && !dto.fecha().isEmpty()) {
            reserva.setFecha(LocalDate.parse(dto.fecha(), DATE_FORMATTER));
        }

        reserva.setHora(dto.hora());
        reserva.setPersonas(dto.personas());
        reserva.setExperiencia(dto.experiencia());
        reserva.setAlergias(dto.alergias());
        reserva.setRequerimientos(dto.requerimientos());
        reserva.setNecesidades(dto.necesidades());

        return reserva;
    }

    public ReservaResponseDTO toResponseDTO(Reserva reserva) {
        if (reserva == null) return null;

        ReservaResponseDTO dto = new ReservaResponseDTO(
            reserva.getId(),
            reserva.getCodigo(),
            reserva.getEstado(),
            reserva.getNombre(),
            reserva.getApellido(),
            reserva.getEmail(),
            reserva.getTelefono(),
            reserva.getFecha().toString(),
            reserva.getHora(),
            reserva.getPersonas(),
            reserva.getExperiencia(),
            reserva.getAlergias(),
            reserva.getRequerimientos(),
            reserva.getNecesidades(),
            reserva.getCreatedAt(),
            reserva.getUpdatedAt()
        );
        return dto;
    }
}