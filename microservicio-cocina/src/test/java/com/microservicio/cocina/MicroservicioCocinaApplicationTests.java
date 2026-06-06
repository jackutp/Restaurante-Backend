package com.microservicio.cocina;

import com.microservicio.cocina.dto.CrearPedidoCocinaRequestDTO;
import com.microservicio.cocina.dto.ItemCocinaRequestDTO;
import com.microservicio.cocina.dto.ItemCocinaResponseDTO;
import com.microservicio.cocina.dto.PedidoCocinaResponseDTO;
import com.microservicio.cocina.entity.ItemCocina;
import com.microservicio.cocina.entity.PedidoCocina;
import com.microservicio.cocina.exception.ConflictException;
import com.microservicio.cocina.exception.ExternalServiceException;
import com.microservicio.cocina.exception.ResourceNotFoundException;
import com.microservicio.cocina.repository.ItemCocinaRepository;
import com.microservicio.cocina.repository.PedidoCocinaRepository;
import com.microservicio.cocina.service.PedidoCocinaService;
import com.microservicio.cocina.service.feign.PedidoFeignClient;
import feign.FeignException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@ActiveProfiles("tests")
@Transactional
class MicroservicioCocinaApplicationTests {
	@Autowired
	private PedidoCocinaService pedidoCocinaService;
	@Autowired
	private PedidoCocinaRepository pedidoRepository;
	@Autowired
	private ItemCocinaRepository itemRepository;
	@MockitoBean
	private PedidoFeignClient pedidoFeignClient;

	private CrearPedidoCocinaRequestDTO buildRequest() {
		ItemCocinaRequestDTO item1 = new ItemCocinaRequestDTO();
		item1.setProductoId(1);
		item1.setNombre("Pizza");
		item1.setCantidad(2);
		item1.setNotas("Sin cebolla");
		ItemCocinaRequestDTO item2 = new ItemCocinaRequestDTO();
		item2.setProductoId(2);
		item2.setNombre("Pasta");
		item2.setCantidad(1);
		item2.setNotas("Extra queso");
		CrearPedidoCocinaRequestDTO request = new CrearPedidoCocinaRequestDTO();
		request.setOrdenId("ORD-001");
		request.setMesaNumero(5);
		request.setHora("12:30");
		request.setItems(List.of(item1, item2));
		return request;
	}
	@Test
	void contextLoads() {
	}

	@Test
	void shouldRecibirPedido() {
		CrearPedidoCocinaRequestDTO request = buildRequest();
		PedidoCocinaResponseDTO response = pedidoCocinaService.recibirPedido(request);
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getOrdenId()).isEqualTo("ORD-001");
		Assertions.assertThat(response.getEstado()).isEqualTo("PENDIENTE");
		Assertions.assertThat(response.getItems()).hasSize(2);
		Assertions.assertThat(pedidoRepository.findByOrdenId("ORD-001")).isPresent();
	}

	@Test
	void shouldRejectPedidoWithoutItems() {
		CrearPedidoCocinaRequestDTO request = new CrearPedidoCocinaRequestDTO();
		request.setOrdenId("ORD-EMPTY");
		request.setMesaNumero(1);
		request.setHora("10:00");
		request.setItems(List.of());
		Assertions.assertThatThrownBy(() ->
						pedidoCocinaService.recibirPedido(request))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("pedido no puede estar vacio");
	}

	@Test
	void shouldGetPedidosPendientes() {
		pedidoCocinaService.recibirPedido(buildRequest());
		List<PedidoCocinaResponseDTO> pendientes = pedidoCocinaService.getPedidosPendientes();
		Assertions.assertThat(pendientes).isNotEmpty();
		Assertions.assertThat(pendientes.get(0).getEstado()).isEqualTo("PENDIENTE");
	}

	@Test
	void shouldGetPedidoByOrdenId() {
		pedidoCocinaService.recibirPedido(buildRequest());
		PedidoCocinaResponseDTO pedido = pedidoCocinaService.getPedidoByOrdenId("ORD-001");
		Assertions.assertThat(pedido).isNotNull();
		Assertions.assertThat(pedido.getMesaNumero()).isEqualTo(5);
	}

	@Test
	void shouldThrowWhenPedidoNotFound() {
		Assertions.assertThatThrownBy(() -> pedidoCocinaService .getPedidoByOrdenId("INVALID"))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldMarcarItemCompletado() {
		pedidoCocinaService.recibirPedido(buildRequest());
		PedidoCocina pedido = pedidoRepository.findByOrdenId("ORD-001").orElseThrow();
		ItemCocina item = pedido.getItems().get(0);
		ItemCocinaResponseDTO response = pedidoCocinaService.marcarItemCompletado(item.getId());
		Assertions.assertThat(response.getCompletado()).isTrue();
		ItemCocina updated = itemRepository.findById(item.getId()).orElseThrow();
		Assertions.assertThat(updated.getCompletado()).isTrue();
	}

	@Test
	void shouldMarkPedidoAsListoWhenAllItemsCompleted() {
		pedidoCocinaService.recibirPedido(buildRequest());
		PedidoCocina pedido = pedidoRepository.findByOrdenId("ORD-001").orElseThrow();
		for (ItemCocina item : pedido.getItems()) {
			pedidoCocinaService
					.marcarItemCompletado(item.getId());
		}
		PedidoCocina updated = pedidoRepository.findByOrdenId("ORD-001").orElseThrow();
		Assertions.assertThat(updated.getEstado()).isEqualTo("LISTO");
		Mockito.verify(pedidoFeignClient)
				.actualizarEstadoPedido(
						Mockito.eq("ORD-001"),
						Mockito.anyMap()
				);
	}

	@Test
	void shouldThrowWhenItemNotFound() {
		Assertions.assertThatThrownBy(() -> pedidoCocinaService.marcarItemCompletado(99999L))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldThrowExternalServiceExceptionWhenFeignFails() {
		pedidoCocinaService.recibirPedido(buildRequest());
		PedidoCocina pedido = pedidoRepository.findByOrdenId("ORD-001").orElseThrow();
		Mockito.doThrow(Mockito.mock(FeignException.class))
				.when(pedidoFeignClient)
				.actualizarEstadoPedido(
						Mockito.anyString(),
						Mockito.anyMap()
				);

		Assertions.assertThatThrownBy(() -> {
			for (ItemCocina item : pedido.getItems()) {
				pedidoCocinaService .marcarItemCompletado(item.getId());
			}
		}).isInstanceOf(ExternalServiceException.class);
	}

	@Test
	void shouldMarcarPedidoServido() {
		pedidoCocinaService.recibirPedido(buildRequest());
		pedidoCocinaService.marcarPedidoServido("ORD-001");
		PedidoCocina pedido = pedidoRepository.findByOrdenId("ORD-001").orElseThrow();
		Assertions.assertThat(pedido.getEstado()).isEqualTo("SERVIDO");
		Mockito.verify(pedidoFeignClient)
				.actualizarEstadoPedido(
						Mockito.eq("ORD-001"),
						Mockito.anyMap()
				);
	}

	@Test
	void shouldGetHistorialPedidos() {
		pedidoCocinaService.recibirPedido(buildRequest());
		pedidoCocinaService.marcarPedidoServido("ORD-001");
		List<PedidoCocina> historial = pedidoCocinaService.getHistorialPedidos();
		Assertions.assertThat(historial).isNotEmpty();
		Assertions.assertThat(historial.get(0).getEstado()).isIn("LISTO", "SERVIDO");
	}
}
