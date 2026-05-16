package com.microservicio.eventos.Services;

import com.microservicio.eventos.Entities.EventoRequest;
import com.microservicio.eventos.Entities.EventoStatus;
import com.microservicio.eventos.dto.EventoRequestDTO;
import com.microservicio.eventos.dto.EventoResponseDTO;
import com.microservicio.eventos.dto.EventoStatusUpdateDTO;
import com.microservicio.eventos.Exceptions.EventoException;
import com.microservicio.eventos.Mapper.EventoMapper;
import com.microservicio.eventos.Repositories.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;

    // Validar fecha (hoy + 3 meses máximo)
    private void validateEventDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusMonths(3);

        if (date.isBefore(today)) {
            throw new EventoException("La fecha del evento no puede ser anterior a hoy");
        }

        if (date.isAfter(maxDate)) {
            throw new EventoException("La fecha del evento no puede ser mayor a 3 meses a partir de hoy");
        }
    }

    // Validar capacidad máxima diaria (opcional)
    private void checkDailyCapacity(LocalDate date) {
        Long eventsCount = eventoRepository.countByDate(date);
        if (eventsCount >= 10) {
            throw new EventoException("Capacidad máxima de eventos alcanzada para esta fecha");
        }
    }

    // Crear nuevo evento
    @Transactional
    public EventoResponseDTO createEvento(EventoRequestDTO dto) {
        // Validar fecha
        validateEventDate(dto.getDate());

        // Validar capacidad (opcional)
        checkDailyCapacity(dto.getDate());

        // Convertir DTO a entidad
        EventoRequest evento = eventoMapper.toEntity(dto);
        evento.setStatus(EventoStatus.PENDIENTE);

        // Guardar
        EventoRequest savedEvento = eventoRepository.save(evento);

        // TODO: Enviar notificaciones (email, etc.)

        return eventoMapper.toResponseDTO(savedEvento);
    }

    // Obtener todos los eventos (paginado)
    public Page<EventoResponseDTO> getAllEventos(Pageable pageable) {
        return eventoRepository.findAll(pageable)
                .map(eventoMapper::toResponseDTO);
    }

    // Obtener evento por ID
    public EventoResponseDTO getEventoById(Long id) {
        EventoRequest evento = eventoRepository.findById(id)
                .orElseThrow(() -> new EventoException("Evento no encontrado con ID: " + id));
        return eventoMapper.toResponseDTO(evento);
    }

    // Actualizar estado del evento
    @Transactional
    public EventoResponseDTO updateEventoStatus(Long id, EventoStatusUpdateDTO updateDTO) {
        EventoRequest evento = eventoRepository.findById(id)
                .orElseThrow(() -> new EventoException("Evento no encontrado con ID: " + id));

        try {
            EventoStatus newStatus = EventoStatus.valueOf(updateDTO.getStatus());
            evento.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new EventoException("Estado inválido. Use: PENDIENTE, RECIBIDO o CANCELADO");
        }

        EventoRequest updatedEvento = eventoRepository.save(evento);


        return eventoMapper.toResponseDTO(updatedEvento);
    }

    // Eliminar evento
    @Transactional
    public void deleteEvento(Long id) {
        EventoRequest evento = eventoRepository.findById(id)
                .orElseThrow(() -> new EventoException("Evento no encontrado con ID: " + id));
        eventoRepository.delete(evento);
    }

    // Filtrar eventos por estado
    public Page<EventoResponseDTO> getEventosByStatus(String status, Pageable pageable) {
        try {
            EventoStatus eventoStatus = EventoStatus.valueOf(status);
            return eventoRepository.findByStatus(eventoStatus, pageable)
                    .map(eventoMapper::toResponseDTO);
        } catch (IllegalArgumentException e) {
            throw new EventoException("Estado inválido. Use: PENDIENTE, RECIBIDO o CANCELADO");
        }
    }

    // Obtener estadísticas
    public Object getEventoStats() {
        List<Object[]> stats = eventoRepository.countByStatus();
        long total = eventoRepository.count();

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> stat[0].toString(),
                        stat -> stat[1]
                ));
    }

    // Buscar eventos por email
    public List<EventoResponseDTO> getEventosByEmail(String email) {
        return eventoRepository.findByEmail(email)
                .stream()
                .map(eventoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Verificar disponibilidad de fecha
    public boolean checkAvailability(LocalDate date) {
        try {
            validateEventDate(date);
            Long count = eventoRepository.countByDate(date);
            return count < 10; // Capacidad máxima 10 eventos por día
        } catch (EventoException e) {
            return false;
        }
    }
}
