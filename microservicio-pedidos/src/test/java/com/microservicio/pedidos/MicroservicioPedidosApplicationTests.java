package com.microservicio.pedidos;

import com.microservicio.pedidos.dto.*;
import com.microservicio.pedidos.entity.EstadoPedido;
import com.microservicio.pedidos.entity.Pedido;
import com.microservicio.pedidos.entity.PedidoItem;
import com.microservicio.pedidos.exception.ConflictException;
import com.microservicio.pedidos.exception.ExternalServiceException;
import com.microservicio.pedidos.exception.ResourceNotFoundException;
import com.microservicio.pedidos.repository.PedidoItemRepository;
import com.microservicio.pedidos.repository.PedidoRepository;
import com.microservicio.pedidos.service.PedidoService;
import com.microservicio.pedidos.service.feign.CocinaFeignClient;
import com.microservicio.pedidos.service.feign.MesaFeignClient;
import com.microservicio.pedidos.service.feign.ProductoFeignClient;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class MicroservicioPedidosApplicationTests {
	@Autowired
	private PedidoService pedidoService;
	@Autowired
	private PedidoRepository pedidoRepository;
	@Autowired
	private PedidoItemRepository pedidoItemRepository;
	@MockitoBean
	private MesaFeignClient mesaFeignClient;
	@MockitoBean
	private ProductoFeignClient productoFeignClient;
	@MockitoBean
	private CocinaFeignClient cocinaFeignClient;

	private CrearPedidoRequestDTO request;

	@Test
	void contextLoads() {
	}

	@BeforeEach
	void setUp() {
		PedidoItemRequestDTO item = new PedidoItemRequestDTO();
		item.setProductoId(1);
		item.setCantidad(2);
		item.setNotas("Sin cebolla");

		request = new CrearPedidoRequestDTO();
		request.setMesaNumero(5);
		request.setItems(List.of(item));

		// Mock stock
		ProductoFeignClient.StockResponse stockResponse =
				new ProductoFeignClient.StockResponse();
		stockResponse.setStock(10);

		when(productoFeignClient.obtenerStockProducto(1))
				.thenReturn(stockResponse);

		// Mock product
		ProductoResponseDTO producto = new ProductoResponseDTO();
		producto.setId(1);
		producto.setNombre("Hamburguesa");
		producto.setPrecio(25.0);

		when(productoFeignClient.obtenerProductoPorId(1)).thenReturn(producto);
	}

	@Test
	void crearPedido_ShouldSavePedidoAndItems() {
		PedidoResponseDTO response = pedidoService.crearPedido(request);
		assertNotNull(response);
		assertNotNull(response.getId());
		assertNotNull(response.getOrdenId());
		assertEquals(5, response.getMesaNumero());
		Assertions.assertEquals(EstadoPedido.PENDIENTE, response.getEstado());


		Pedido savedPedido = pedidoRepository.findById(response.getId()).orElseThrow();

		assertEquals(1, savedPedido.getItems().size());

		PedidoItem item = savedPedido.getItems().get(0);

		Assertions.assertEquals("Hamburguesa", item.getNombre());
		Assertions.assertEquals(25.0, item.getPrecio());
		assertEquals(2, item.getCantidad());
	}

	private void assertEquals(int i, Integer mesaNumero) {
	}

	@Test
	void crearPedido_ShouldUpdateMesa() {

		PedidoResponseDTO response = pedidoService.crearPedido(request);

		verify(mesaFeignClient).actualizarEstadoMesa(
				eq(5),
				argThat(dto ->
						dto.getEstado().equals("OCUPADO")
								&& dto.getTotalActual().equals(50.0)
								&& dto.getOrdenActualId().equals(response.getOrdenId())
				)
		);
	}

	@Test
	void crearPedido_ShouldDiscountStock() {
		pedidoService.crearPedido(request);
		verify(productoFeignClient).actualizarStock(
				eq(1),
				argThat(map -> map.get("stock").equals(8))
		);
	}

	@Test
	void crearPedido_ShouldSendPedidoToCocina() {
		pedidoService.crearPedido(request);
		verify(cocinaFeignClient).enviarPedidoACocina(
				argThat(dto ->
						dto.getMesaNumero().equals(5)
								&& dto.getItems().size() == 1
								&& dto.getItems().get(0).getNombre().equals("Hamburguesa")
				)
		);
	}

	@Test
	void crearPedido_ShouldThrowConflictException_WhenStockInsufficient() {

		ProductoFeignClient.StockResponse stockResponse = new ProductoFeignClient.StockResponse();
		stockResponse.setStock(1);
		when(productoFeignClient.obtenerStockProducto(1)).thenReturn(stockResponse);
		ConflictException exception = assertThrows(ConflictException.class,
				() -> pedidoService.crearPedido(request)
		);
		assertTrue(exception.getMessage().contains("Stock insuficiente"));
	}

	@Test
	void crearPedido_ShouldThrowResourceNotFound_WhenProductoNotFound() {
		when(productoFeignClient.obtenerProductoPorId(1)).thenReturn(null);
		ResourceNotFoundException exception = assertThrows(
				ResourceNotFoundException.class,
				() -> pedidoService.crearPedido(request)
		);
		assertTrue(exception.getMessage().contains("Producto no encontrado"));
	}

	@Test
	void crearPedido_ShouldThrowExternalServiceException_WhenMesaFails() {
		doThrow(FeignException.class)
				.when(mesaFeignClient)
				.actualizarEstadoMesa(anyInt(), any());

		ExternalServiceException exception = assertThrows(
				ExternalServiceException.class,
				() -> pedidoService.crearPedido(request)
		);

		assertTrue(exception.getMessage().contains("Error al actualizar mesa"));
	}

	@Test
	void crearPedido_ShouldThrowExternalServiceException_WhenStockUpdateFails() {
		doThrow(FeignException.class)
				.when(productoFeignClient)
				.actualizarStock(anyInt(), anyMap());

		ExternalServiceException exception = assertThrows(
				ExternalServiceException.class,
				() -> pedidoService.crearPedido(request)
		);

		assertTrue(exception.getMessage().contains("Error al actualizar stock"));
	}

	@Test
	void crearPedido_ShouldThrowExternalServiceException_WhenCocinaFails() {
		doThrow(FeignException.class)
				.when(cocinaFeignClient)
				.enviarPedidoACocina(any());
		ExternalServiceException exception = assertThrows(
				ExternalServiceException.class,
				() -> pedidoService.crearPedido(request)
		);
		assertTrue(exception.getMessage().contains("Error al enviar a cocina"));
	}

	@Test
	void getAllPedidos_ShouldReturnPedidos() {
		pedidoService.crearPedido(request);
		List<PedidoResponseDTO> pedidos = pedidoService.getAllPedidos();
		assertFalse(pedidos.isEmpty());
		assertEquals(1, pedidos.size());
	}

	@Test
	void getPedidoById_ShouldReturnPedido() {
		pedidoService.crearPedido(request);
		Pedido pedido = pedidoRepository.findAll().get(0);
		PedidoResponseDTO response = pedidoService.getPedidoById(pedido.getId());
		assertNotNull(response);
		Assertions.assertEquals(pedido.getId(), response.getId());
	}

	@Test
	void getPedidoById_ShouldThrowResourceNotFound() {
		assertThrows(
				ResourceNotFoundException.class,
				() -> pedidoService.getPedidoById(999L)
		);
	}

	@Test
	void getPedidoByOrdenId_ShouldReturnPedido() {
		PedidoResponseDTO created = pedidoService.crearPedido(request);
		PedidoResponseDTO response = pedidoService.getPedidoByOrdenId(created.getOrdenId());
		Assertions.assertEquals(created.getOrdenId(), response.getOrdenId());
	}

	@Test
	void getPedidosByEstado_ShouldReturnPedidos() {
		pedidoService.crearPedido(request);
		List<PedidoResponseDTO> pedidos = pedidoService.getPedidosByEstado(EstadoPedido.PENDIENTE);
		assertEquals(1, pedidos.size());
	}

	@Test
	void actualizarEstado_ShouldUpdateEstado() {
		PedidoResponseDTO created = pedidoService.crearPedido(request);
		Pedido pedido = pedidoRepository.findById(created.getId()).orElseThrow();
		ActualizarEstadoRequestDTO updateRequest = new ActualizarEstadoRequestDTO();
		updateRequest.setEstado(EstadoPedido.COMPLETADO);
		PedidoResponseDTO response = pedidoService.actualizarEstado(
						pedido.getId(),
						updateRequest
				);

		Assertions.assertEquals(EstadoPedido.COMPLETADO, response.getEstado());

		verify(mesaFeignClient).actualizarEstadoMesa(
				eq(5),
				argThat(dto ->
						dto.getEstado().equals("DISPONIBLE")
				)
		);
	}

	@Test
	void actualizarItemCompletado_ShouldUpdateItem() {
		pedidoService.crearPedido(request);
		PedidoItem item = pedidoItemRepository.findAll().get(0);
		ActualizarItemCompletadoRequestDTO updateRequest = new ActualizarItemCompletadoRequestDTO();
		updateRequest.setCompletado(true);
		PedidoItemResponseDTO response =
				pedidoService.actualizarItemCompletado(
						item.getId(),
						updateRequest
				);

		assertTrue(response.getCompletado());
	}

	@Test
	void eliminarPedido_ShouldDeletePedido() {
		PedidoResponseDTO created = pedidoService.crearPedido(request);
		Pedido pedido = pedidoRepository.findById(created.getId()).orElseThrow();
		pedidoService.eliminarPedido(pedido.getId());
		assertEquals(0, pedidoRepository.findAll().size());
		verify(mesaFeignClient).actualizarEstadoMesa(
				eq(5),
				argThat(dto ->
						dto.getEstado().equals("DISPONIBLE")
				)
		);
	}

	@Test
	void getMetricas_ShouldReturnMetrics() {
		pedidoService.crearPedido(request);
		MetricasPedidosResponseDTO response = pedidoService.getMetricas();
		assertNotNull(response);
		assertNotNull(response.getOrdenesPorEstado());
		assertNotNull(response.getProductosTop());
	}

	@Test
	void actualizarEstadoPorOrdenId_ShouldUpdateEstado() {
		PedidoResponseDTO created = pedidoService.crearPedido(request);
		ActualizarEstadoRequestDTO updateRequest = new ActualizarEstadoRequestDTO();
		updateRequest.setEstado(EstadoPedido.EN_PREPARACION);
		PedidoResponseDTO response = pedidoService.actualizarEstadoPorOrdenId(
						created.getOrdenId(),
						updateRequest
				);
		Assertions.assertEquals(EstadoPedido.EN_PREPARACION, response.getEstado());
	}
}
