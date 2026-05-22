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
import java.util.*;
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
        comprobante.setCreatedAt(LocalDateTime.now());
        comprobante = comprobanteRepository.save(comprobante);

        ComprobanteResponseDTO response = new ComprobanteResponseDTO();
        response.setId(comprobante.getId());
        response.setTipo(comprobante.getTipo());
        response.setNumeroCompleto(comprobante.getNumeroCompleto());
        response.setPdfUrl(comprobante.getPdfUrl());
        response.setTotal(comprobante.getTotal());

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
            System.err.println("Error al liberar mesa " + numeroMesa + ": " + e.getMessage());
        }
    }

    private void actualizarEstadoPedido(String ordenId) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("estado", "COMPLETADO");
            pedidoFeignClient.actualizarEstadoPedido(ordenId, request);
            System.out.println("✅ Pedido " + ordenId + " marcado como COMPLETADO");
        } catch (Exception e) {
            System.err.println("Error al actualizar pedido " + ordenId + ": " + e.getMessage());
        }
    }

    //Para metricas
    public MetricasPagosResponseDTO getMetricas() {
        MetricasPagosResponseDTO metricas = new MetricasPagosResponseDTO();

        try {
            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String hoyInicio = ahora.withHour(0).withMinute(0).withSecond(0).format(formatter);
            String hoyFin = ahora.withHour(23).withMinute(59).withSecond(59).format(formatter);

            // 1. Ventas del día
            Double ventasDelDia = comprobanteRepository.sumTotalByDateRangeNative(hoyInicio, hoyFin);
            metricas.setVentasDelDia(ventasDelDia != null ? ventasDelDia : 0.0);

            // 2. Ventas últimos 7 días
            List<Double> ventasSemanales = new ArrayList<>();
            List<String> dias = new ArrayList<>();
            DateTimeFormatter diaFormatter = DateTimeFormatter.ofPattern("EEE", new Locale("es", "ES"));

            for (int i = 6; i >= 0; i--) {
                LocalDateTime dia = ahora.minusDays(i);
                String inicio = dia.withHour(0).withMinute(0).withSecond(0).format(formatter);
                String fin = dia.withHour(23).withMinute(59).withSecond(59).format(formatter);

                Double total = comprobanteRepository.sumTotalByDateRangeNative(inicio, fin);
                ventasSemanales.add(total != null ? total : 0.0);
                dias.add(dia.format(diaFormatter));
            }
            metricas.setVentasUltimos7Dias(ventasSemanales);
            metricas.setDiasSemana(dias);

            // 3. Ventas por hora
            List<Object[]> ventasPorHoraRaw = comprobanteRepository.sumTotalByHourNative(hoyInicio, hoyFin);
            List<MetricasPagosResponseDTO.VentaPorHoraDTO> ventasPorHora = new ArrayList<>();
            for (Object[] row : ventasPorHoraRaw) {
                ventasPorHora.add(new MetricasPagosResponseDTO.VentaPorHoraDTO(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).doubleValue()
                ));
            }
            metricas.setVentasPorHora(ventasPorHora);

            // 4. Datos de mesas
            try {
                List<MesaResponseDTO> mesas = mesaFeignClient.getAllMesas();
                long ocupadas = mesas.stream().filter(m -> "OCUPADO".equals(m.getEstado())).count();
                metricas.setMesasOcupadas((int) ocupadas);
                metricas.setTotalMesas(mesas.size());
                metricas.setOcupacionPorcentaje(mesas.size() > 0 ? (ocupadas * 100.0 / mesas.size()) : 0.0);
            } catch (Exception e) {
                metricas.setMesasOcupadas(0);
                metricas.setTotalMesas(0);
                metricas.setOcupacionPorcentaje(0.0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            metricas.setVentasDelDia(0.0);
            metricas.setVentasUltimos7Dias(new ArrayList<>());
            metricas.setDiasSemana(new ArrayList<>());
            metricas.setVentasPorHora(new ArrayList<>());
            metricas.setMesasOcupadas(0);
            metricas.setTotalMesas(0);
            metricas.setOcupacionPorcentaje(0.0);
        }

        return metricas;
    }
}