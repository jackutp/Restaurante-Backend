package com.microservicio.pagos;

import com.microservicio.pagos.dto.MetricasPagosResponseDTO;
import com.microservicio.pagos.dto.ProcesarPagoRequestDTO;
import com.microservicio.pagos.dto.ProcesarPagoResponseDTO;
import com.microservicio.pagos.exception.ExternalServiceException;
import com.microservicio.pagos.repository.ComprobanteRepository;
import com.microservicio.pagos.repository.PagoRepository;
import com.microservicio.pagos.service.PagoService;
import com.microservicio.pagos.service.PdfGeneratorService;
import com.microservicio.pagos.service.feign.MesaFeignClient;
import com.microservicio.pagos.service.feign.PedidoFeignClient;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class MicroservicioPagosApplicationTests {
	@Autowired
	private PagoService pagoService;
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
	void procesarPago_ShouldSavePago() {
		ProcesarPagoResponseDTO response = pagoService.procesarPago(request);
		assertNotNull(response);
		assertNotNull(response.getPagoId());
		assertEquals("ORD-100", response.getOrdenId());
		assertEquals(150.0, response.getTotal());
		assertNull(response.getComprobante());
		assertEquals(4, pagoRepository.findAll().size());
	}

	@Test
	void procesarPago_ShouldGenerateComprobante() {
		request.setTipoComprobante("EFECTIVO");
		ProcesarPagoResponseDTO response = pagoService.procesarPago(request);
		assertNotNull(response.getComprobante());
		assertEquals("EFECTIVO", response.getComprobante().getTipo());
		assertEquals(4, comprobanteRepository.findAll().size());
	}

	@Test
	void procesarPago_ShouldThrowIllegalArgument() {
		request.setMetodo("BITCOIN");
		assertThrows(IllegalArgumentException.class, () -> pagoService.procesarPago(request));
	}

	@Test
	void procesarPago_ShouldThrowExternalServiceException_WhenMesaFails() {
		doThrow(FeignException.class).when(mesaFeignClient).liberarMesa(anyInt(), anyMap());

		ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> pagoService.procesarPago(request));
		assertEquals("No se pudo liberar la mesa: Servicio no disponible", exception.getMessage());
	}
	@Test
	void procesarPago_ShouldThrowExternalServiceException_WhenPedidoFails() {
		doThrow(FeignException.class)
				.when(pedidoFeignClient)
				.actualizarEstadoPedido(anyString(), anyMap());
		ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> pagoService.procesarPago(request));
		assertTrue(exception.getMessage().contains("Error al actualizar pedido"));
	}
	@Test
	void getMetricas_ShouldReturnMetrics() {
		MetricasPagosResponseDTO response = pagoService.getMetricas();
		assertNotNull(response);
		assertNotNull(response.getVentasUltimos7Dias());
		assertNotNull(response.getDiasSemana());
		assertNotNull(response.getVentasPorHora());
	}
	@Test
	void getMetricas_ShouldFallback_WhenMesaServiceFails() {
		when(mesaFeignClient.getAllMesas()).thenThrow(FeignException.class);
		MetricasPagosResponseDTO response = pagoService.getMetricas();
		assertEquals(0, response.getMesasOcupadas());
		assertEquals(0, response.getTotalMesas());
		assertEquals(0.0, response.getOcupacionPorcentaje());
	}
	@Test
	void generarComprobantePdf_ShouldGenerateFile() {
		String path = pdfGeneratorService.generarComprobantePdf(
						"BOLETA",
						"B001-000001",
						5,
						150.0,
						null,
						null
				);
		assertNotNull(path);
		File file = new File(path);
		assertTrue(file.exists());
	}

}
