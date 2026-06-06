package com.microservicio.solicitudes.controller;

import com.microservicio.solicitudes.dto.ApiResponseDTO;
import com.microservicio.solicitudes.dto.SolicitudDTO;
import com.microservicio.solicitudes.enums.EstadoSolicitud;
import com.microservicio.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/solicitudes")  // ← Cambiado: sin /api porque el gateway lo quita
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO> crearSolicitud(@Valid @RequestBody SolicitudDTO solicitudDTO) {
        log.info("POST /solicitudes - Creando solicitud");
        try {
            SolicitudDTO created = solicitudService.crearSolicitud(solicitudDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDTO(true, "Solicitud creada exitosamente", created));
        } catch (Exception e) {
            log.error("Error al crear solicitud: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO(false, "Error al crear solicitud: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoSolicitud estado) {
        log.info("PUT /solicitudes/{}/estado?estado={}", id, estado);
        try {
            SolicitudDTO updated = solicitudService.actualizarEstado(id, estado);
            return ResponseEntity.ok(new ApiResponseDTO(true, "Estado actualizado exitosamente", updated));
        } catch (Exception e) {
            log.error("Error al actualizar estado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> obtenerSolicitud(@PathVariable Long id) {
        log.info("GET /solicitudes/{}", id);
        try {
            SolicitudDTO solicitud = solicitudService.obtenerSolicitud(id);
            return ResponseEntity.ok(new ApiResponseDTO(true, "Solicitud encontrada", solicitud));
        } catch (Exception e) {
            log.error("Error al obtener solicitud: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO(false, e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO> listarTodas() {
        log.info("GET /solicitudes - Listando todas");
        List<SolicitudDTO> solicitudes = solicitudService.listarTodas();
        return ResponseEntity.ok(new ApiResponseDTO(true, "Listado de solicitudes", solicitudes));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponseDTO> listarPorEstado(@PathVariable EstadoSolicitud estado) {
        log.info("GET /solicitudes/estado/{}", estado);
        List<SolicitudDTO> solicitudes = solicitudService.listarPorEstado(estado);
        return ResponseEntity.ok(new ApiResponseDTO(true, "Solicitudes con estado " + estado, solicitudes));
    }
}