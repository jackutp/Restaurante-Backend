package com.microservicio.cocina;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.cocina.dto.CrearPedidoCocinaRequestDTO;
import com.microservicio.cocina.dto.ItemCocinaRequestDTO;
import com.microservicio.cocina.entity.ItemCocina;
import com.microservicio.cocina.entity.PedidoCocina;
import com.microservicio.cocina.repository.ItemCocinaRepository;
import com.microservicio.cocina.repository.PedidoCocinaRepository;
import com.microservicio.cocina.service.feign.PedidoFeignClient;
import feign.FeignException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("tests")
@Transactional
@AutoConfigureMockMvc
public class CocinaRestTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedidoCocinaRepository pedidoRepository;

    @Autowired
    private ItemCocinaRepository itemRepository;

    @MockitoBean
    private PedidoFeignClient pedidoFeignClient;

    private CrearPedidoCocinaRequestDTO crearPedidoRequest;

    @BeforeEach
    void setUp() {
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

        crearPedidoRequest = new CrearPedidoCocinaRequestDTO();
        crearPedidoRequest.setOrdenId("ORD-001");
        crearPedidoRequest.setMesaNumero(10);
        crearPedidoRequest.setHora("14:30");
        crearPedidoRequest.setItems(List.of(item1, item2));
    }

    @Test
    void recibirPedido_ShouldCreatePedido() throws Exception {

        mockMvc.perform(post("/cocina/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearPedidoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ordenId").value("ORD-001"))
                .andExpect(jsonPath("$.mesaNumero").value(10))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].nombre").value("Pizza"));

        Optional<PedidoCocina> pedidoGuardado = pedidoRepository.findByOrdenId("ORD-001");
        assertTrue(pedidoGuardado.isPresent());
        assertEquals(2, pedidoGuardado.get().getItems().size());
    }

    @Test
    void recibirPedido_ShouldReturnConflict_WhenItemsAreEmpty() throws Exception {
        crearPedidoRequest.setItems(Collections.emptyList());
        mockMvc.perform(post("/cocina/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearPedidoRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El pedido no puede estar vacio"));
    }

    @Test
    void getPedidosPendientes_ShouldReturnPendingPedidos() throws Exception {
        PedidoCocina pedido = new PedidoCocina(
                "ORD-100",
                5,
                "13:00"
        );
        pedido.setEstado("PENDIENTE");
        ItemCocina item = new ItemCocina(
                1,
                "Hamburguesa",
                1,
                "Sin tomate"
        );
        item.setPedido(pedido);
        pedido.setItems(List.of(item));
        pedidoRepository.save(pedido);
        mockMvc.perform(get("/cocina/pedidos/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ordenId").value("ORD-100"))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void getPedidoByOrdenId_ShouldReturnPedido() throws Exception {
        PedidoCocina pedido = new PedidoCocina(
                "ORD-200",
                8,
                "15:00"
        );
        pedido.setEstado("PENDIENTE");
        ItemCocina item = new ItemCocina(
                3,
                "Lasagna",
                1,
                "Normal"
        );
        item.setPedido(pedido);
        pedido.setItems(List.of(item));
        pedidoRepository.save(pedido);
        mockMvc.perform(get("/cocina/pedidos/ORD-200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordenId").value("ORD-200"))
                .andExpect(jsonPath("$.mesaNumero").value(8));
    }

    @Test
    void getPedidoByOrdenId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/cocina/pedidos/NO-EXISTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pedido no encontrado: NO-EXISTE"));
    }

    @Test
    void marcarItemCompletado_ShouldMarkItemAsCompleted() throws Exception {
        PedidoCocina pedido = new PedidoCocina(
                "ORD-300",
                2,
                "16:00"
        );
        pedido.setEstado("PENDIENTE");
        ItemCocina item = new ItemCocina(
                4,
                "Ensalada",
                1,
                "Sin sal"
        );
        item.setPedido(pedido);
        pedido.setItems(List.of(item));
        pedidoRepository.save(pedido);
        Long itemId = pedido.getItems().get(0).getId();
        doNothing().when(pedidoFeignClient).actualizarEstadoPedido(anyString(), anyMap());
        mockMvc.perform(patch("/cocina/items/" + itemId + "/completado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completado").value(true));
        ItemCocina updated = itemRepository.findById(itemId).orElseThrow();
        assertTrue(updated.getCompletado());
        PedidoCocina pedidoActualizado = pedidoRepository.findByOrdenId("ORD-300").orElseThrow();
        assertEquals("LISTO", pedidoActualizado.getEstado());
    }

    @Test
    void marcarItemCompletado_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(patch("/cocina/items/999/completado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item no encontrado: 999"));
    }

    @Test
    void marcarItemCompletado_ShouldReturnServiceUnavailable_WhenFeignFails() throws Exception {
        PedidoCocina pedido = new PedidoCocina(
                "ORD-400",
                3,
                "18:00"
        );
        pedido.setEstado("PENDIENTE");
        ItemCocina item = new ItemCocina(
                5,
                "Pollo",
                1,
                "Picante"
        );

        item.setPedido(pedido);
        pedido.setItems(List.of(item));
        pedidoRepository.save(pedido);
        Long itemId = pedido.getItems().get(0).getId();
        doThrow(FeignException.class)
                .when(pedidoFeignClient)
                .actualizarEstadoPedido(anyString(), anyMap());
        mockMvc.perform(patch("/cocina/items/" + itemId + "/completado"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Servicio Pedidos no disponible"));
    }

    @Test
    void marcarPedidoServido_ShouldUpdatePedido() throws Exception {
        PedidoCocina pedido = new PedidoCocina(
                "ORD-500",
                7,
                "19:00"
        );
        pedido.setEstado("LISTO");
        pedidoRepository.save(pedido);
        doNothing().when(pedidoFeignClient).actualizarEstadoPedido(anyString(), anyMap());
        mockMvc.perform(patch("/cocina/pedidos/ORD-500/servido")).andExpect(status().isOk());
        PedidoCocina updated = pedidoRepository.findByOrdenId("ORD-500").orElseThrow();
        assertEquals("SERVIDO", updated.getEstado());
    }

    @Test
    void marcarPedidoServido_ShouldReturnServiceUnavailable_WhenFeignFails() throws Exception {
        PedidoCocina pedido = new PedidoCocina(
                "ORD-600",
                9,
                "20:00"
        );
        pedido.setEstado("LISTO");
        pedidoRepository.save(pedido);
        doThrow(FeignException.class)
                .when(pedidoFeignClient)
                .actualizarEstadoPedido(anyString(), anyMap());
        mockMvc.perform(patch("/cocina/pedidos/ORD-600/servido"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Servicio Pedidos no disponible"));
    }

    @Test
    void getHistorialPedidos_ShouldReturnOnlyListoAndServido() throws Exception {
        PedidoCocina pedido1 = new PedidoCocina(
                "ORD-701",
                1,
                "12:00"
        );
        pedido1.setEstado("LISTO");
        PedidoCocina pedido2 = new PedidoCocina(
                "ORD-702",
                2,
                "13:00"
        );
        pedido2.setEstado("SERVIDO");
        PedidoCocina pedido3 = new PedidoCocina(
                "ORD-703",
                3,
                "14:00"
        );
        pedido3.setEstado("PENDIENTE");
        pedidoRepository.saveAll(List.of(pedido1, pedido2, pedido3));
        mockMvc.perform(get("/cocina/pedidos/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(14));
    }
}
