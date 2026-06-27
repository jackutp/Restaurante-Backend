package com.microservicio.pedidos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.pedidos.dto.*;
import com.microservicio.pedidos.entity.EstadoPedido;
import com.microservicio.pedidos.exception.ResourceNotFoundException;
import com.microservicio.pedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("tests")
@Transactional
public class PedidoRestTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private PedidoService pedidoService;

    private PedidoResponseDTO pedidoResponse;
    private PedidoItemResponseDTO itemResponse;

    @BeforeEach
    void setUp() {
        itemResponse = PedidoItemResponseDTO.builder()
                .id(1L)
                .productoId(1)
                .nombre("Hamburguesa")
                .precio(25.0)
                .cantidad(2)
                .notas("Sin cebolla")
                .completado(false)
                .build();

        pedidoResponse = PedidoResponseDTO.builder()
                .id(1L)
                .ordenId("ORD-123ABC")
                .mesaNumero(5)
                .hora("14:30")
                .estado(EstadoPedido.PENDIENTE)
                .items(List.of(itemResponse))
                .build();
    }

    @Test
    void getAllPedidos_ShouldReturnOk() throws Exception {
        when(pedidoService.getAllPedidos()).thenReturn(List.of(pedidoResponse));

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].ordenId").value("ORD-123ABC"))
                .andExpect(jsonPath("$[0].mesaNumero").value(5));
    }

    @Test
    void getPedidoById_ShouldReturnOk() throws Exception {
        when(pedidoService.getPedidoById(1L)).thenReturn(pedidoResponse);
        mockMvc.perform(get("/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ordenId").value("ORD-123ABC"));
    }

    @Test
    void getPedidoByOrdenId_ShouldReturnOk() throws Exception {
        when(pedidoService.getPedidoByOrdenId("ORD-123ABC")).thenReturn(pedidoResponse);
        mockMvc.perform(get("/pedidos/orden/ORD-123ABC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordenId").value("ORD-123ABC"));
    }

    @Test
    void getPedidosByEstado_ShouldReturnOk() throws Exception {
        when(pedidoService.getPedidosByEstado(EstadoPedido.PENDIENTE)).thenReturn(List.of(pedidoResponse));

        mockMvc.perform(get("/pedidos/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void crearPedido_ShouldReturnCreated() throws Exception {
        CrearPedidoRequestDTO request = new CrearPedidoRequestDTO();
        PedidoItemRequestDTO item = new PedidoItemRequestDTO();
        item.setProductoId(1);
        item.setCantidad(2);
        item.setNotas("Sin cebolla");
        request.setMesaNumero(5);
        request.setItems(List.of(item));
        when(pedidoService.crearPedido(any(CrearPedidoRequestDTO.class)))
                .thenReturn(pedidoResponse);

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ordenId").value("ORD-123ABC"))
                .andExpect(jsonPath("$.mesaNumero").value(5));
    }

    @Test
    void crearPedido_ShouldReturnBadRequest_WhenMesaNumeroInvalid() throws Exception {

        CrearPedidoRequestDTO request = new CrearPedidoRequestDTO();
        request.setMesaNumero(0);
        request.setItems(List.of());

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarEstado_ShouldReturnOk() throws Exception {

        ActualizarEstadoRequestDTO request = new ActualizarEstadoRequestDTO();
        request.setEstado(EstadoPedido.COMPLETADO);

        PedidoResponseDTO updated = PedidoResponseDTO.builder()
                .id(1L)
                .ordenId("ORD-123ABC")
                .mesaNumero(5)
                .hora("14:30")
                .estado(EstadoPedido.COMPLETADO)
                .items(List.of(itemResponse))
                .build();

        when(pedidoService.actualizarEstado(eq(1L), any(ActualizarEstadoRequestDTO.class)))
                .thenReturn(updated);

        mockMvc.perform(patch("/pedidos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }

    @Test
    void actualizarItemCompletado_ShouldReturnOk() throws Exception {

        ActualizarItemCompletadoRequestDTO request =
                new ActualizarItemCompletadoRequestDTO();

        request.setCompletado(true);

        PedidoItemResponseDTO updatedItem = PedidoItemResponseDTO.builder()
                .id(1L)
                .productoId(1)
                .nombre("Hamburguesa")
                .precio(25.0)
                .cantidad(2)
                .notas("Sin cebolla")
                .completado(true)
                .build();

        when(pedidoService.actualizarItemCompletado(
                eq(1L),
                any(ActualizarItemCompletadoRequestDTO.class)
        )).thenReturn(updatedItem);

        mockMvc.perform(patch("/pedidos/items/1/completado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completado").value(true));
    }

    @Test
    void eliminarPedido_ShouldReturnNoContent() throws Exception {
        doNothing().when(pedidoService).eliminarPedido(1L);
        mockMvc.perform(delete("/pedidos/1")).andExpect(status().isNoContent());
        verify(pedidoService).eliminarPedido(1L);
    }

    @Test
    void getMetricas_ShouldReturnOk() throws Exception {
        MetricasPedidosResponseDTO metricas = new MetricasPedidosResponseDTO();
        Map<String, Long> estados = new HashMap<>();
        estados.put("PENDIENTE", 2L);

        List<MetricasPedidosResponseDTO.ProductoTopDTO> productos =
                List.of(
                        new MetricasPedidosResponseDTO.ProductoTopDTO(
                                "Hamburguesa",
                                10L,
                                250.0
                        )
                );

        metricas.setOrdenesCompletadas(5L);
        metricas.setOrdenesPorEstado(estados);
        metricas.setProductosTop(productos);

        when(pedidoService.getMetricas())
                .thenReturn(metricas);

        mockMvc.perform(get("/pedidos/metricas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordenesCompletadas").value(5))
                .andExpect(jsonPath("$.ordenesPorEstado.PENDIENTE").value(2))
                .andExpect(jsonPath("$.productosTop[0].nombre")
                        .value("Hamburguesa"));
    }

    @Test
    void actualizarEstadoPorOrdenId_ShouldReturnOk() throws Exception {

        ActualizarEstadoRequestDTO request =
                new ActualizarEstadoRequestDTO();

        request.setEstado(EstadoPedido.EN_PREPARACION);

        PedidoResponseDTO updated = PedidoResponseDTO.builder()
                .id(1L)
                .ordenId("ORD-123ABC")
                .mesaNumero(5)
                .hora("14:30")
                .estado(EstadoPedido.EN_PREPARACION)
                .items(List.of(itemResponse))
                .build();

        when(pedidoService.actualizarEstadoPorOrdenId(
                eq("ORD-123ABC"),
                any(ActualizarEstadoRequestDTO.class)
        )).thenReturn(updated);

        mockMvc.perform(post("/pedidos/orden/ORD-123ABC/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado")
                        .value("EN_PREPARACION"));
    }

    @Test
    void getPedidoById_ShouldReturnNotFound() throws Exception {

        when(pedidoService.getPedidoById(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Pedido no encontrado"
                ));

        mockMvc.perform(get("/pedidos/999"))
                .andExpect(status().isNotFound());
    }
}
