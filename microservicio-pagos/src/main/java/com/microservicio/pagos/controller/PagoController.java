package com.microservicio.pagos.controller;

import com.microservicio.pagos.dto.ProcesarPagoRequestDTO;
import com.microservicio.pagos.dto.ProcesarPagoResponseDTO;
import com.microservicio.pagos.entity.Comprobante;
import com.microservicio.pagos.repository.ComprobanteRepository;
import com.microservicio.pagos.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final ComprobanteRepository comprobanteRepository;

    public PagoController(PagoService pagoService,
                          ComprobanteRepository comprobanteRepository) {
        this.pagoService = pagoService;
        this.comprobanteRepository = comprobanteRepository;
    }

    @PostMapping("/procesar")
    public ResponseEntity<ProcesarPagoResponseDTO> procesarPago(
            @Valid @RequestBody ProcesarPagoRequestDTO request) {
        ProcesarPagoResponseDTO response = pagoService.procesarPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ NUEVO: Listar todos los comprobantes
    @GetMapping("/comprobantes")
    public ResponseEntity<List<Comprobante>> listarComprobantes() {
        List<Comprobante> comprobantes = comprobanteRepository.findAllByOrderByIdDesc();
        return ResponseEntity.ok(comprobantes);
    }

    // ✅ NUEVO: Descargar PDF de un comprobante
    @GetMapping("/comprobantes/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        try {
            Comprobante comprobante = comprobanteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));

            Path pdfPath = Paths.get(comprobante.getPdfUrl());

            if (!Files.exists(pdfPath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdfBytes = Files.readAllBytes(pdfPath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    comprobante.getNumeroCompleto() + ".pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}