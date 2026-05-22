package com.Restaurante.reservas;

import com.Restaurante.reservas.dto.MesaRequestDTO;
import com.Restaurante.reservas.dto.ReservaRequestDTO;
import com.Restaurante.reservas.dto.ReservaRespuestaDTO;
import com.Restaurante.reservas.entities.Reserva;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.sql.Date;
import java.sql.Time;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
public class ReservaRestTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private ReservaRequestDTO  buildReservaRequest(){
        return new ReservaRequestDTO(
                3L,
                2,
                4,
                Date.valueOf("2026-05-24"),
                Time.valueOf("15:30:00"),
                Reserva.menuTipo.CARTA,
                null
        );
    }

    @Test
    void shouldCreateReserva() throws Exception {
        ReservaRequestDTO requestDTO =  buildReservaRequest();
        mockMvc.perform(post("/reservas/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cantidadClientes").value(4))
                .andExpect(jsonPath("$.menu").value("CARTA"));
    }

    @Test
    void shouldFindReservaById() throws Exception{
        ReservaRequestDTO requestDTO =  buildReservaRequest();
        String response = mockMvc.perform(post("/reservas/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        ReservaRespuestaDTO saved = objectMapper.readValue(response, ReservaRespuestaDTO.class);
        mockMvc.perform(get("/reservas/search/" + saved.reservaId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservaId").value(saved.reservaId()));
    }

    @Test
    void shouldFindMesaById() throws Exception{
        mockMvc.perform(get("/reservas/searchMesa/" + 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mesa_id").value(3L));
    }

    @Test
    void shouldDeleteReserva() throws Exception {
        ReservaRequestDTO requestDTO =  buildReservaRequest();
        String response = mockMvc.perform(post("/reservas/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        ReservaRespuestaDTO saved = objectMapper.readValue(response, ReservaRespuestaDTO.class);
        mockMvc.perform(delete("/reservas/delete/" + saved.reservaId()))
                .andExpect(status().isOk());
    }
    @Test
    void shouldReturn404WhenMesaNotFound() throws Exception {
        ReservaRequestDTO requestDTO = new ReservaRequestDTO(
                999L,
                2,
                4,
                Date.valueOf("2026-05-24"),
                Time.valueOf("15:300:00"),
                Reserva.menuTipo.CARTA,
                null
        );
        mockMvc.perform(post("/reservas/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }
}
