package com.microservicio.Mermas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.Mermas.Entities.TipoMerma;
import com.microservicio.Mermas.Services.feign.InsumoFeignClient;
import com.microservicio.Mermas.Services.feign.ProductoFeignClient;
import com.microservicio.Mermas.dto.InsumoDTO;
import com.microservicio.Mermas.dto.MermaDTO;
import com.microservicio.Mermas.dto.MermaRequestDTO;
import com.microservicio.Mermas.dto.ProductoDTO;
import com.microservicio.Mermas.exception.ExternalServiceException;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("tests")
@Transactional
public class MermasRestTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ProductoFeignClient productoFeignClient;
    @MockitoBean
    private InsumoFeignClient insumoFeignClient;
    private MermaRequestDTO buildRequestDTO() {
        MermaRequestDTO dto = new MermaRequestDTO();
        dto.setTipoMerma(TipoMerma.INSUMO);
        dto.setNombreMerma("Harina vencida");
        dto.setCantidad("5");
        dto.setMotivo("Producto vencido");
        dto.setReferenciaId(1);
        dto.setUnidadMedida("KG");
        return dto;
    }
    @Test
    void shouldCreateMerma() throws Exception {
        MermaRequestDTO dto = buildRequestDTO();
        mockMvc.perform(post("/mermas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mermaid").exists())
                .andExpect(jsonPath("$.nombreMerma").value("Harina vencida"))
                .andExpect(jsonPath("$.cantidad").value("5"));
    }

    @Test
    void shouldGetAllMermas() throws Exception {
        MermaRequestDTO dto = buildRequestDTO();
        mockMvc.perform(post("/mermas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        mockMvc.perform(get("/mermas/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void shouldGetMermaById() throws Exception {
        MermaRequestDTO dto = buildRequestDTO();
        String response = mockMvc.perform(post("/mermas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        MermaDTO saved = objectMapper.readValue(response, MermaDTO.class);
        mockMvc.perform(get("/mermas/" + saved.getMermaid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mermaid").value(saved.getMermaid()))
                .andExpect(jsonPath("$.nombreMerma").value("Harina vencida"));
    }

    @Test
    void shouldReturn404WhenMermaNotFound() throws Exception {
        mockMvc.perform(get("/mermas/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindByTipo() throws Exception {
        MermaRequestDTO dto = buildRequestDTO();
        mockMvc.perform(post("/mermas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        mockMvc.perform(get("/mermas/tipo/INSUMO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldUpdateMerma() throws Exception {
        MermaRequestDTO dto = buildRequestDTO();
        String response = mockMvc.perform(post("/mermas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        MermaDTO saved = objectMapper.readValue(response, MermaDTO.class);
        MermaRequestDTO updatedDto = buildRequestDTO();
        updatedDto.setNombreMerma("Aceite derramado");
        updatedDto.setCantidad("10");
        mockMvc.perform(put("/mermas/" + saved.getMermaid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreMerma")
                        .value("Aceite derramado"))
                .andExpect(jsonPath("$.cantidad")
                        .value("10"));
    }

    @Test
    void shouldDeleteMerma() throws Exception {
        MermaRequestDTO dto = buildRequestDTO();
        String response = mockMvc.perform(post("/mermas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        MermaDTO saved = objectMapper.readValue(response, MermaDTO.class);
        mockMvc.perform(delete("/mermas/" + saved.getMermaid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Merma eliminada exitosamente"));
    }

    @Test
    void shouldRejectBlankNombreMerma() throws Exception {
        MermaRequestDTO dto = buildRequestDTO();
        dto.setNombreMerma("");
        mockMvc.perform(post("/mermas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankCantidad() throws Exception {
        MermaRequestDTO dto = buildRequestDTO();
        dto.setCantidad("");
        mockMvc.perform(post("/mermas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankMotivo() throws Exception {
        MermaRequestDTO dto = buildRequestDTO();
        dto.setMotivo("");
        mockMvc.perform(post("/mermas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNullTipoMerma() throws Exception {
        MermaRequestDTO dto = buildRequestDTO();
        dto.setTipoMerma(null);
        mockMvc.perform(post("/mermas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetProductos() throws Exception {
        ProductoDTO producto = new ProductoDTO();
        producto.setProductoid(1);
        producto.setNombre("Pizza");

        Mockito.when(productoFeignClient.getAllProductos()).thenReturn(List.of(producto));
        mockMvc.perform(get("/mermas/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre")
                        .value("Pizza"));
    }

    @Test
    void shouldGetInsumos() throws Exception {
        InsumoDTO insumo = new InsumoDTO();
        insumo.setInsumoid(1);
        insumo.setNombre("Harina");
        Mockito.when(insumoFeignClient.getAllInsumos()).thenReturn(List.of(insumo));

        mockMvc.perform(get("/mermas/insumos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Harina"));
    }

    @Test
    void shouldReturn503WhenProductosServiceFails() throws Exception {
        Mockito.when(productoFeignClient.getAllProductos()).thenThrow(Mockito.mock(FeignException.class));

        mockMvc.perform(get("/mermas/productos"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void shouldReturn503WhenInsumosServiceFails() throws Exception {
        Mockito.when(insumoFeignClient.getAllInsumos()).thenThrow(Mockito.mock(FeignException.class));

        mockMvc.perform(get("/mermas/insumos"))
                .andExpect(status().isServiceUnavailable());
    }
}
