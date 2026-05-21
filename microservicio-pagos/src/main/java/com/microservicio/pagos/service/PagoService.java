package com.microservicio.pagos.service;

import com.microservicio.pagos.dto.*;
import com.microservicio.pagos.entity.*;
import com.microservicio.pagos.repository.PagoRepository;
import com.microservicio.pagos.repository.ComprobanteRepository;
import com.microservicio.pagos.service.feign.MesaFeignClient;
import com.microservicio.pagos.service.feign.PedidoFeignClient;
import com.microservicio.pagos.utils.NumeracionComprobanteUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final MesaFeignClient mesaFeignClient;
    private final PedidoFeignClient pedidoFeignClient;
    private final PdfGeneratorService pdfGeneratorService;
    private final NumeracionComprobanteUtil numeracionUtil;

    public PagoService(PagoRepository pagoRepository,
                       ComprobanteRepository comprobanteRepository,
                       MesaFeignClient mesaFeignClient,
                       PedidoFeignClient pedidoFeignClient,
                       PdfGeneratorService pdfGeneratorService,
                       NumeracionComprobanteUtil numeracionUtil) {
        this.pagoRepository = pagoRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.mesaFeignClient = mesaFeignClient;
        this.pedidoFeignClient = pedidoFeignClient;
        this.pdfGeneratorService = pdfGeneratorService;
        this.numeracionUtil = numeracionUtil;
    }

    @Transactional
    public ProcesarPagoResponseDTO procesarPago(ProcesarPagoRequestDTO request) {

        // 1. Guardar pago
        MetodoPago metodo = MetodoPago.valueOf(request.getMetodo());
        Pago pago = new Pago(request.getOrdenId(), request.getMesaNumero(),
                request.getTotal(), metodo);
        pago = pagoRepository.save(pago);

        // 2. Generar comprobante
        ComprobanteResponseDTO comprobanteDTO = null;
        if (request.getTipoComprobante() != null) {
            comprobanteDTO = generarComprobante(request);
        }

        // 3. Liberar mesa (cambiar a DISPONIBLE y total = 0)
        liberarMesa(request.getMesaNumero());

        // 4. Actualizar estado del pedido a COMPLETADO
        actualizarEstadoPedido(request.getOrdenId());

        // 5. Construir respuesta
        ProcesarPagoResponseDTO response = new ProcesarPagoResponseDTO();
        response.setPagoId(pago.getId());
        response.setOrdenId(pago.getOrdenId());
        response.setMesaNumero(pago.getMesaNumero());
        response.setTotal(pago.getTotal());
        response.setMetodo(pago.getMetodo().toString());
        response.setEstado(pago.getEstado());
        response.setComprobante(comprobanteDTO);

        return response;
    }

    private ComprobanteResponseDTO generarComprobante(ProcesarPagoRequestDTO request) {
        String tipo = request.getTipoComprobante();
        String numeroCompleto = numeracionUtil.generarNumeroComprobante(tipo);
        String serie = numeroCompleto.substring(0, 4);
        Integer correlativo = numeracionUtil.obtenerCorrelativo(serie);

        // Generar PDF
        String pdfPath = pdfGeneratorService.generarComprobantePdf(
                tipo, numeroCompleto, request.getMesaNumero(),
                request.getTotal(), request.getRuc(), request.getRazonSocial()
        );

        // Guardar comprobante en BD
        Comprobante comprobante = new Comprobante();
        comprobante.setTipo(tipo);
        comprobante.setSerie(serie);
        comprobante.setCorrelativo(correlativo);
        comprobante.setNumeroCompleto(numeroCompleto);
        comprobante.setRuc(request.getRuc());
        comprobante.setRazonSocial(request.getRazonSocial());
        comprobante.setOrdenId(request.getOrdenId());
        comprobante.setMesaNumero(request.getMesaNumero());
        comprobante.setTotal(request.getTotal());
        comprobante.setPdfUrl(pdfPath);
        comprobante.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        comprobante = comprobanteRepository.save(comprobante);

        ComprobanteResponseDTO response = new ComprobanteResponseDTO();
        response.setId(comprobante.getId());
        response.setTipo(comprobante.getTipo());
        response.setNumeroCompleto(comprobante.getNumeroCompleto());
        response.setPdfUrl(comprobante.getPdfUrl());
        response.setTotal(comprobante.getTotal());
        response.setFecha(comprobante.getCreatedAt());

        return response;
    }

    private void liberarMesa(Integer numeroMesa) {
        try {
            // Cambiar estado a DISPONIBLE
            Map<String, Object> estadoRequest = new HashMap<>();
            estadoRequest.put("estado", "DISPONIBLE");
            estadoRequest.put("totalActual", 0.0);
            estadoRequest.put("ordenActualId", null);
            mesaFeignClient.liberarMesa(numeroMesa, estadoRequest);

            // Resetear total a 0
            Map<String, Double> totalRequest = new HashMap<>();
            totalRequest.put("total", 0.0);
            mesaFeignClient.resetearTotal(numeroMesa, totalRequest);

            System.out.println("✅ Mesa " + numeroMesa + " liberada correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error al liberar mesa " + numeroMesa + ": " + e.getMessage());
        }
    }

    private void actualizarEstadoPedido(String ordenId) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("estado", "COMPLETADO");
            pedidoFeignClient.actualizarEstadoPedido(ordenId, request);
            System.out.println("✅ Pedido " + ordenId + " marcado como COMPLETADO");
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar pedido " + ordenId + ": " + e.getMessage());
        }
    }
}