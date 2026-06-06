package com.microservicio.Insumos;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.Insumos.Entities.EstadoInsumo;
import com.microservicio.Insumos.Entities.UnidadMedida;
import com.microservicio.Insumos.dto.InsumoDTO;
import com.microservicio.Insumos.dto.InsumoRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("tests")
@Transactional
public class InsumoRestTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private InsumoRequestDTO buildRequestDTO() {
        InsumoRequestDTO dto = new InsumoRequestDTO();
        dto.setNombre("Insumo Test");
        dto.setUnidadMedida(UnidadMedida.L);
        dto.setStock(50);
        dto.setEstadoInsumo(EstadoInsumo.DISPONIBLE);
        return dto;
    }

    @Test
    void shouldCreateInsumo() throws Exception {

        InsumoRequestDTO dto = buildRequestDTO();

        mockMvc.perform(post("/insumos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.insumoid").exists())
                .andExpect(jsonPath("$.nombre").value("Insumo Test"))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.estadoInsumo")
                        .value("DISPONIBLE"));
    }

    @Test
    void shouldFindById() throws Exception {

        InsumoRequestDTO dto = buildRequestDTO();

        String response = mockMvc.perform(post("/insumos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        InsumoDTO saved =
                objectMapper.readValue(response, InsumoDTO.class);

        mockMvc.perform(get("/insumos/" + saved.getInsumoid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.insumoid")
                        .value(saved.getInsumoid()))
                .andExpect(jsonPath("$.nombre")
                        .value("Insumo Test"));
    }

    @Test
    void shouldReturnAllInsumos() throws Exception {

        InsumoRequestDTO dto = buildRequestDTO();

        mockMvc.perform(post("/insumos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(get("/insumos/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void shouldFindByNombre() throws Exception {

        InsumoRequestDTO dto = buildRequestDTO();

        mockMvc.perform(post("/insumos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(get("/insumos/search")
                        .param("nombre", "Insumo Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre")
                        .value("Insumo Test"));
    }

    @Test
    void shouldFindByEstado() throws Exception {

        InsumoRequestDTO dto = buildRequestDTO();

        mockMvc.perform(post("/insumos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(get("/insumos/estado/DISPONIBLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].estadoInsumo")
                        .value("DISPONIBLE"));
    }

    @Test
    void shouldUpdateInsumo() throws Exception {

        InsumoRequestDTO dto = buildRequestDTO();

        String response = mockMvc.perform(post("/insumos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        InsumoDTO saved =
                objectMapper.readValue(response, InsumoDTO.class);

        InsumoRequestDTO updated = buildRequestDTO();
        updated.setNombre("Azucar");
        updated.setStock(100);

        mockMvc.perform(put("/insumos/" + saved.getInsumoid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre")
                        .value("Azucar"))
                .andExpect(jsonPath("$.stock")
                        .value(100));
    }

    @Test
    void shouldDeleteInsumo() throws Exception {

        InsumoRequestDTO dto = buildRequestDTO();

        String response = mockMvc.perform(post("/insumos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        InsumoDTO saved =
                objectMapper.readValue(response, InsumoDTO.class);

        mockMvc.perform(delete("/insumos/" + saved.getInsumoid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Insumo eliminado exitosamente"));
    }

    @Test
    void shouldUpdateStock() throws Exception {

        InsumoRequestDTO dto = buildRequestDTO();

        String response = mockMvc.perform(post("/insumos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        InsumoDTO saved =
                objectMapper.readValue(response, InsumoDTO.class);

        Map<String, Integer> request = new HashMap<>();
        request.put("stock", 200);

        mockMvc.perform(patch("/insumos/" + saved.getInsumoid() + "/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock")
                        .value(200));
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {

        mockMvc.perform(get("/insumos/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectDuplicateName() throws Exception {

        InsumoRequestDTO dto = buildRequestDTO();

        mockMvc.perform(post("/insumos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(post("/insumos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNegativeStock() throws Exception {

        Map<String, Integer> request = new HashMap<>();
        request.put("stock", -10);

        mockMvc.perform(patch("/insumos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankNombre() throws Exception {

        InsumoRequestDTO dto = buildRequestDTO();
        dto.setNombre("");

        mockMvc.perform(post("/insumos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNullUnidadMedida() throws Exception {

        InsumoRequestDTO dto = buildRequestDTO();
        dto.setUnidadMedida(null);

        mockMvc.perform(post("/insumos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
