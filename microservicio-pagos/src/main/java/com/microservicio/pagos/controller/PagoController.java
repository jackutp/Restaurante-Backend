package com.microservicio.pagos.controller;

import com.microservicio.pagos.dto.MetricasPagosResponseDTO;
import com.microservicio.pagos.dto.ProcesarPagoRequestDTO;
import com.microservicio.pagos.dto.ProcesarPagoResponseDTO;
import com.microservicio.pagos.entity.Comprobante;
import com.microservicio.pagos.exception.ResourceNotFoundException;
import com.microservicio.pagos.repository.ComprobanteRepository;
import com.microservicio.pagos.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private PagoService pagoService;
    @Autowired
    private ComprobanteRepository comprobanteRepository;

    @PostMapping("/procesar")
    public ResponseEntity<ProcesarPagoResponseDTO> procesarPago(
            @Valid @RequestBody ProcesarPagoRequestDTO request) {
        ProcesarPagoResponseDTO response = pagoService.procesarPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Listar comprobantes
    @GetMapping("/comprobantes")
    public ResponseEntity<List<Comprobante>> listarComprobantes() {
        List<Comprobante> comprobantes = comprobanteRepository.findAllByOrderByIdDesc();
        return ResponseEntity.ok(comprobantes);
    }

    //  Descargar PDF de un comprobante
    @GetMapping("/comprobantes/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Comprobante comprobante = comprobanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado"));
        byte[] pdfBytes = pagoService.descargarComprobante(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                comprobante.getNumeroCompleto() + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @GetMapping("/metricas")
    public ResponseEntity<MetricasPagosResponseDTO> getMetricas() {
        return ResponseEntity.ok(pagoService.getMetricas());
    }

}