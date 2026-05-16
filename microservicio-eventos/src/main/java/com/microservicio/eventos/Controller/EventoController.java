package com.microservicio.eventos.Controller;

import com.microservicio.eventos.dto.EventoRequestDTO;
import com.microservicio.eventos.dto.EventoResponseDTO;
import com.microservicio.eventos.dto.EventoStatusUpdateDTO;
import com.microservicio.eventos.Services.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    @PostMapping
    public ResponseEntity<EventoResponseDTO> createEvento(@Valid @RequestBody EventoRequestDTO dto) {
        EventoResponseDTO response = eventoService.createEvento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<EventoResponseDTO>> getAllEventos(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(eventoService.getAllEventos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> getEventoById(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.getEventoById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EventoResponseDTO> updateEventoStatus(
            @PathVariable Long id,
            @Valid @RequestBody EventoStatusUpdateDTO updateDTO) {
        return ResponseEntity.ok(eventoService.updateEventoStatus(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable Long id) {
        eventoService.deleteEvento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<EventoResponseDTO>> getEventosByStatus(
            @PathVariable String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(eventoService.getEventosByStatus(status, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getEventoStats() {
        return ResponseEntity.ok(eventoService.getEventoStats());
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventoResponseDTO>> searchByEmail(@RequestParam String email) {
        return ResponseEntity.ok(eventoService.getEventosByEmail(email));
    }

    // Verificar disponibilidad de fecha
    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean available = eventoService.checkAvailability(date);
        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("date", date);
        return ResponseEntity.ok(response);
    }


}