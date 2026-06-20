package com.microservicio.cambios.controller;

import com.microservicio.cambios.dto.CambioDTO;
import com.microservicio.cambios.dto.CrearCambioDTO;
import com.microservicio.cambios.dto.jira.ApiResponseDTO;
import com.microservicio.cambios.enums.EstadoCambio;
import com.microservicio.cambios.enums.RiesgoCambio;
import com.microservicio.cambios.enums.TipoCambio;
import com.microservicio.cambios.service.CambioService;
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
@RequestMapping("/cambios")
@RequiredArgsConstructor
public class CambioController {

    private final CambioService cambioService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponseDTO> crearSolicitud(@Valid @RequestBody CrearCambioDTO dto) {
        log.info("POST /cambios/create - Tipo: {}", dto.tipoCambio());
        try {
            CambioDTO created = cambioService.crearSolicitud(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDTO(true, "Cambio creado exitosamente", created));
        } catch (Exception e) {
            log.error("Error al crear solicitud: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO(false, "Error al crear cambio: " + e.getMessage(), null));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDTO> listarTodas() {
        log.info("GET /cambios/all - Listando todas");
        List<CambioDTO> cambios = cambioService.listarTodas();
        return ResponseEntity.ok(new ApiResponseDTO(true, "Listado de cambios", cambios));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> obtenerSolicitud(@PathVariable Long id) {
        log.info("GET /cambios/{}", id);
        try {
            CambioDTO cambio = cambioService.obtenerSolicitud(id);
            return ResponseEntity.ok(new ApiResponseDTO(true, "Cambio encontrado", cambio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO(false, e.getMessage(), null));
        }
    }

    @GetMapping("/codigo/{codigoTicket}")
    public ResponseEntity<ApiResponseDTO> obtenerPorCodigo(@PathVariable String codigoTicket) {
        log.info("GET /cambios/codigo/{}", codigoTicket);
        try {
            CambioDTO cambio = cambioService.obtenerPorCodigoTicket(codigoTicket);
            return ResponseEntity.ok(new ApiResponseDTO(true, "Cambio encontrado", cambio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoCambio estado) {
        log.info("PUT /cambios/{}/estado?estado={}", id, estado);
        try {
            CambioDTO updated = cambioService.actualizarEstado(id, estado);
            return ResponseEntity.ok(new ApiResponseDTO(true, "Estado actualizado exitosamente", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO(false, e.getMessage(), null));
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponseDTO> listarPorEstado(@PathVariable EstadoCambio estado) {
        log.info("GET /cambios/estado/{}", estado);
        List<CambioDTO> cambios = cambioService.listarPorEstado(estado);
        return ResponseEntity.ok(new ApiResponseDTO(true, "Cambios con estado " + estado, cambios));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<ApiResponseDTO> listarPorTipo(@PathVariable TipoCambio tipo) {
        log.info("GET /cambios/tipo/{}", tipo);
        List<CambioDTO> cambios = cambioService.listarPorTipo(tipo);
        return ResponseEntity.ok(new ApiResponseDTO(true, "Cambios de tipo " + tipo, cambios));
    }

    @GetMapping("/tipo/{tipo}/estado/{estado}")
    public ResponseEntity<ApiResponseDTO> listarPorTipoYEstado(
            @PathVariable TipoCambio tipo,
            @PathVariable EstadoCambio estado) {
        log.info("GET /solicitudes/tipo/{}/estado/{}", tipo, estado);
        List<CambioDTO> cambios = cambioService.listarPorTipoYEstado(tipo, estado);
        return ResponseEntity.ok(new ApiResponseDTO(true, "Cambios filtrados", cambios));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponseDTO> obtenerEstadisticas() {
        log.info("GET /cambios/estadisticas");

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("total", cambioService.contarTotal());
        estadisticas.put("pendientes", cambioService.contarPorEstado(EstadoCambio.PENDIENTE));
        estadisticas.put("en_revision", cambioService.contarPorEstado(EstadoCambio.EN_REVISION));
        estadisticas.put("aprobado", cambioService.contarPorEstado(EstadoCambio.APROBADO));
        estadisticas.put("rechazado", cambioService.contarPorEstado(EstadoCambio.RECHAZADO));
        estadisticas.put("en_implementacion", cambioService.contarPorEstado(EstadoCambio.EN_IMPLEMENTACION));
        estadisticas.put("implementado", cambioService.contarPorEstado(EstadoCambio.IMPLEMENTADO));
        estadisticas.put("rollback", cambioService.contarPorEstado(EstadoCambio.ROLLBACK));
        estadisticas.put("cerrado", cambioService.contarPorEstado(EstadoCambio.CERRADO));

        Map<String, Long> porTipo = new HashMap<>();
        porTipo.put("normal", cambioService.contarPorTipo(TipoCambio.NORMAL));
        porTipo.put("emergencia", cambioService.contarPorTipo(TipoCambio.EMERGENCIA));
        porTipo.put("repetitivo", cambioService.contarPorTipo(TipoCambio.REPETITIVO));
        estadisticas.put("por_tipo", porTipo);

        return ResponseEntity.ok(new ApiResponseDTO(true, "Estadísticas de solicitudes", estadisticas));
    }

    @GetMapping("/configuracion")
    public ResponseEntity<ApiResponseDTO> getConfiguracion() {
        Map<String, Object> config = new HashMap<>();
        config.put("tiposSolicitud", TipoCambio.values());
        config.put("estados", EstadoCambio.values());
        config.put("riesgos", RiesgoCambio.values());
        config.put("flujoTrabajo", Map.of(
                "PENDIENTE", List.of("EN_REVISION"),
                "EN_REVISION", List.of("APROBADO", "RECHAZADO"),
                "APROBADO", List.of("EN_IMPLEMENTACION"),
                "EN_IMPLEMENTACION", List.of("IMPLEMENTADO", "ROLLBACK"),
                "IMPLEMENTADO", List.of("CERRADO"),
                "ROLLBACK", List.of("CERRADO"),
                "RECHAZADO", List.of(),
                "CERRADO", List.of()
        ));
        return ResponseEntity.ok(new ApiResponseDTO(true, "Configuración del sistema", config));
    }

    //new
    // En SolicitudController.java - Agregar estos endpoints

    @PutMapping("/{id}/responsable")
    public ResponseEntity<ApiResponseDTO> actualizarResponsable(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        log.info("PUT /cambios/{}/responsable", id);
        try {
            String responsable = body.get("responsable");
            CambioDTO updated = cambioService.actualizarResponsable(id, responsable);
            return ResponseEntity.ok(new ApiResponseDTO(true, "Responsable actualizado", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO(false, e.getMessage(), null));
        }
    }
}