package com.microservicio.solicitudes.controller;

import com.microservicio.solicitudes.dto.ApiResponseDTO;
import com.microservicio.solicitudes.dto.CrearSolicitudDTO;
import com.microservicio.solicitudes.dto.SolicitudDTO;
import com.microservicio.solicitudes.enums.EstadoSolicitud;
import com.microservicio.solicitudes.enums.TipoSolicitud;
import com.microservicio.solicitudes.enums.Prioridad;
import com.microservicio.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO> crearSolicitud(@Valid @RequestBody CrearSolicitudDTO dto) {
        log.info("POST /solicitudes - Tipo: {}", dto.getTipoSolicitud());
        try {
            SolicitudDTO created = solicitudService.crearSolicitud(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDTO(true, "Solicitud creada exitosamente", created));
        } catch (Exception e) {
            log.error("Error al crear solicitud: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO(false, "Error al crear solicitud: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO> listarTodas() {
        log.info("GET /solicitudes - Listando todas");
        List<SolicitudDTO> solicitudes = solicitudService.listarTodas();
        return ResponseEntity.ok(new ApiResponseDTO(true, "Listado de solicitudes", solicitudes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> obtenerSolicitud(@PathVariable Long id) {
        log.info("GET /solicitudes/{}", id);
        try {
            SolicitudDTO solicitud = solicitudService.obtenerSolicitud(id);
            return ResponseEntity.ok(new ApiResponseDTO(true, "Solicitud encontrada", solicitud));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO(false, e.getMessage(), null));
        }
    }

    @GetMapping("/codigo/{codigoTicket}")
    public ResponseEntity<ApiResponseDTO> obtenerPorCodigo(@PathVariable String codigoTicket) {
        log.info("GET /solicitudes/codigo/{}", codigoTicket);
        try {
            SolicitudDTO solicitud = solicitudService.obtenerPorCodigoTicket(codigoTicket);
            return ResponseEntity.ok(new ApiResponseDTO(true, "Solicitud encontrada", solicitud));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO(false, e.getMessage(), null));
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO(false, e.getMessage(), null));
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponseDTO> listarPorEstado(@PathVariable EstadoSolicitud estado) {
        log.info("GET /solicitudes/estado/{}", estado);
        List<SolicitudDTO> solicitudes = solicitudService.listarPorEstado(estado);
        return ResponseEntity.ok(new ApiResponseDTO(true, "Solicitudes con estado " + estado, solicitudes));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<ApiResponseDTO> listarPorTipo(@PathVariable TipoSolicitud tipo) {
        log.info("GET /solicitudes/tipo/{}", tipo);
        List<SolicitudDTO> solicitudes = solicitudService.listarPorTipo(tipo);
        return ResponseEntity.ok(new ApiResponseDTO(true, "Solicitudes de tipo " + tipo.getNombre(), solicitudes));
    }

    @GetMapping("/tipo/{tipo}/estado/{estado}")
    public ResponseEntity<ApiResponseDTO> listarPorTipoYEstado(
            @PathVariable TipoSolicitud tipo,
            @PathVariable EstadoSolicitud estado) {
        log.info("GET /solicitudes/tipo/{}/estado/{}", tipo, estado);
        List<SolicitudDTO> solicitudes = solicitudService.listarPorTipoYEstado(tipo, estado);
        return ResponseEntity.ok(new ApiResponseDTO(true, "Solicitudes filtradas", solicitudes));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponseDTO> obtenerEstadisticas() {
        log.info("GET /solicitudes/estadisticas");

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("total", solicitudService.contarTotal());
        estadisticas.put("pendientes", solicitudService.contarPorEstado(EstadoSolicitud.PENDIENTE));
        estadisticas.put("en_proceso", solicitudService.contarPorEstado(EstadoSolicitud.EN_PROCESO));
        estadisticas.put("completadas", solicitudService.contarPorEstado(EstadoSolicitud.COMPLETADA));
        estadisticas.put("rechazadas", solicitudService.contarPorEstado(EstadoSolicitud.RECHAZADA));

        Map<String, Long> porTipo = new HashMap<>();
        porTipo.put("servicio", solicitudService.contarPorTipo(TipoSolicitud.SERVICIO));
        porTipo.put("informacion", solicitudService.contarPorTipo(TipoSolicitud.INFORMACION));
        porTipo.put("acceso", solicitudService.contarPorTipo(TipoSolicitud.ACCESO));
        estadisticas.put("por_tipo", porTipo);

        return ResponseEntity.ok(new ApiResponseDTO(true, "Estadísticas de solicitudes", estadisticas));
    }

    @GetMapping("/configuracion")
    public ResponseEntity<ApiResponseDTO> getConfiguracion() {
        Map<String, Object> config = new HashMap<>();
        config.put("tiposSolicitud", TipoSolicitud.values());
        config.put("estados", EstadoSolicitud.values());
        config.put("prioridades", Prioridad.values());
        config.put("flujoTrabajo", Map.of(
                "PENDIENTE", List.of("EN_PROCESO", "RECHAZADA"),
                "EN_PROCESO", List.of("COMPLETADA", "RECHAZADA"),
                "COMPLETADA", List.of(),
                "RECHAZADA", List.of("PENDIENTE")
        ));
        return ResponseEntity.ok(new ApiResponseDTO(true, "Configuración del sistema", config));
    }

    //new
    // En SolicitudController.java - Agregar estos endpoints

    @PutMapping("/{id}/responsable")
    public ResponseEntity<ApiResponseDTO> actualizarResponsable(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        log.info("PUT /solicitudes/{}/responsable", id);
        try {
            String responsable = body.get("responsable");
            SolicitudDTO updated = solicitudService.actualizarResponsable(id, responsable);
            return ResponseEntity.ok(new ApiResponseDTO(true, "Responsable actualizado", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/resolucion")
    public ResponseEntity<ApiResponseDTO> actualizarResolucion(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        log.info("PUT /solicitudes/{}/resolucion", id);
        try {
            String resolucion = body.get("resolucion");
            SolicitudDTO updated = solicitudService.actualizarResolucion(id, resolucion);
            return ResponseEntity.ok(new ApiResponseDTO(true, "Resolución actualizada", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO(false, e.getMessage(), null));
        }
    }
}