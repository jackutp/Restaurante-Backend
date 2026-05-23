package com.microservicio.pagos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.pagos.dto.ProcesarPagoRequestDTO;
import com.microservicio.pagos.entity.Comprobante;
import com.microservicio.pagos.repository.ComprobanteRepository;
import com.microservicio.pagos.repository.PagoRepository;
import com.microservicio.pagos.service.PdfGeneratorService;
import com.microservicio.pagos.service.feign.MesaFeignClient;
import com.microservicio.pagos.service.feign.PedidoFeignClient;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
public class PagosRestTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private ComprobanteRepository comprobanteRepository;
    @Autowired
    private PdfGeneratorService pdfGeneratorService;
    @MockitoBean
    private MesaFeignClient mesaFeignClient;
    @MockitoBean
    private PedidoFeignClient pedidoFeignClient;

    private ProcesarPagoRequestDTO request;

    @BeforeEach
    void setUp() {

        request = new ProcesarPagoRequestDTO();

        request.setOrdenId("ORD-100");
        request.setMesaNumero(5);
        request.setTotal(150.0);
        request.setMetodo("EFECTIVO");
    }

    @Test
    void contextLoads() {

    }

    @Test
    void procesarPago_ShouldReturnCreated() throws Exception {

        mockMvc.perform(
                        post("/pagos/procesar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pagoId").exists())
                .andExpect(jsonPath("$.ordenId").value("ORD-100"))
                .andExpect(jsonPath("$.mesaNumero").value(5))
                .andExpect(jsonPath("$.total").value(150.0))
                .andExpect(jsonPath("$.metodo").value("EFECTIVO"))
                .andExpect(jsonPath("$.comprobante").doesNotExist());
    }

    @Test
    void procesarPago_ShouldGenerateComprobante() throws Exception {

        request.setTipoComprobante("BOLETA");

        mockMvc.perform(
                        post("/pagos/procesar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comprobante").exists())
                .andExpect(jsonPath("$.comprobante.tipo").value("BOLETA"))
                .andExpect(jsonPath("$.comprobante.numeroCompleto").exists())
                .andExpect(jsonPath("$.comprobante.pdfUrl").exists());
    }

    @Test
    void procesarPago_ShouldReturnBadRequest_WhenMetodoInvalido() throws Exception {

        request.setMetodo("BITCOIN");

        mockMvc.perform(
                        post("/pagos/procesar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("No enum constant com.microservicio.pagos.entity.MetodoPago.BITCOIN"));
    }

    @Test
    void procesarPago_ShouldReturnBadRequest_WhenRequestInvalid() throws Exception {

        request.setOrdenId(null);

        mockMvc.perform(
                        post("/pagos/procesar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ordenId")
                        .value("La ordenId es obligatoria"));
    }

    @Test
    void procesarPago_ShouldReturnServiceUnavailable_WhenMesaFails() throws Exception {
        doThrow(FeignException.class)
                .when(mesaFeignClient)
                .liberarMesa(anyInt(), anyMap());

        mockMvc.perform(
                        post("/pagos/procesar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message")
                        .value("No se pudo liberar la mesa: Servicio no disponible"));
    }

    @Test
    void procesarPago_ShouldReturnServiceUnavailable_WhenPedidoFails() throws Exception {
        doThrow(FeignException.class)
                .when(pedidoFeignClient)
                .actualizarEstadoPedido(anyString(), anyMap());

        mockMvc.perform(
                        post("/pagos/procesar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message")
                        .value("Error al actualizar pedido: ORD-100: Servicio no disponible"));
    }

    @Test
    void listarComprobantes_ShouldReturnList() throws Exception {
        Comprobante comprobante = new Comprobante();
        comprobante.setTipo("BOLETA");
        comprobante.setSerie("B001");
        comprobante.setCorrelativo(1);
        comprobante.setNumeroCompleto("B001-000001");
        comprobante.setOrdenId("ORD-500");
        comprobante.setMesaNumero(3);
        comprobante.setTotal(100.0);
        comprobante.setPdfUrl("test.pdf");
        comprobante.setCreatedAt(LocalDateTime.now());
        comprobanteRepository.save(comprobante);

        mockMvc.perform(get("/pagos/comprobantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].tipo").value("BOLETA"));
    }

    @Test
    void descargarPdf_ShouldReturnPdf() throws Exception {

        String path = pdfGeneratorService.generarComprobantePdf(
                "BOLETA",
                "B001-999999",
                5,
                120.0,
                null,
                null
        );

        Comprobante comprobante = new Comprobante();

        comprobante.setTipo("BOLETA");
        comprobante.setSerie("B001");
        comprobante.setCorrelativo(999999);
        comprobante.setNumeroCompleto("B001-999999");
        comprobante.setOrdenId("ORD-999");
        comprobante.setMesaNumero(5);
        comprobante.setTotal(120.0);
        comprobante.setPdfUrl(path);
        comprobante.setCreatedAt(LocalDateTime.now());

        comprobante = comprobanteRepository.save(comprobante);

        mockMvc.perform(get("/pagos/comprobantes/" + comprobante.getId() + "/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_PDF_VALUE
                ));
    }

    @Test
    void descargarPdf_ShouldReturnNotFound_WhenFileDoesNotExist() throws Exception {

        Comprobante comprobante = new Comprobante();

        comprobante.setTipo("BOLETA");
        comprobante.setSerie("B001");
        comprobante.setCorrelativo(10);
        comprobante.setNumeroCompleto("B001-000010");
        comprobante.setOrdenId("ORD-404");
        comprobante.setMesaNumero(1);
        comprobante.setTotal(50.0);
        comprobante.setPdfUrl("archivo-inexistente.pdf");
        comprobante.setCreatedAt(LocalDateTime.now());

        comprobante = comprobanteRepository.save(comprobante);

        mockMvc.perform(get("/pagos/comprobantes/" + comprobante.getId() + "/pdf"))
                .andExpect(status().isNotFound());
    }

    @Test
    void descargarPdf_ShouldReturnInternalServerError_WhenComprobanteNotExists() throws Exception {

        mockMvc.perform(get("/pagos/comprobantes/99999/pdf"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getMetricas_ShouldReturnMetrics() throws Exception {

        mockMvc.perform(get("/pagos/metricas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventasUltimos7Dias").exists())
                .andExpect(jsonPath("$.diasSemana").exists())
                .andExpect(jsonPath("$.ventasPorHora").exists());
    }

    @Test
    void getMetricas_ShouldFallback_WhenMesaServiceFails() throws Exception {

        when(mesaFeignClient.getAllMesas())
                .thenThrow(FeignException.class);

        mockMvc.perform(get("/pagos/metricas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mesasOcupadas").value(0))
                .andExpect(jsonPath("$.totalMesas").value(0))
                .andExpect(jsonPath("$.ocupacionPorcentaje").value(0.0));
    }
}
