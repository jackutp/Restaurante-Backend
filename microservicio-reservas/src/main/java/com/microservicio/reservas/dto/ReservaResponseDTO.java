package com.microservicio.reservas.dto;

import com.microservicio.reservas.entity.EstadoReserva;
import java.time.LocalDateTime;

public record ReservaResponseDTO (
     Long id,
     String codigo,
     EstadoReserva estado,
     String nombre,
     String apellido,
     String email,
     String telefono,
     String fecha,  // ← String
     String hora,
     Integer personas,
     String experiencia,
     String alergias,
     String requerimientos,
     String necesidades,
     LocalDateTime createdAt,
     LocalDateTime updatedAt
){}