package com.microservicio.eventos;

import com.microservicio.eventos.Services.EventoService;
import com.microservicio.eventos.dto.EventoRequestDTO;
import com.microservicio.eventos.dto.EventoResponseDTO;
import com.microservicio.eventos.dto.EventoStatusUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@RequiredArgsConstructor
public class EventosRestTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final EventoService eventoService;

    private EventoRequestDTO buildEventoRequestDto(){
        EventoRequestDTO dto = new EventoRequestDTO();
        dto.setName("Juan");
        dto.setLastName("Perez");
        dto.setEmail("juan@test.com");
        dto.setPhone("+51999999999");
        dto.setCompany("Acme");
        dto.setDate(LocalDate.now().plusDays(5));
        dto.setAttendees(50);
        dto.setComments("Evento empresarial importante.");
        dto.setAgeConfirmed(true);
        dto.setPrivacyAccepted(true);
        dto.setMarketingAccepted(false);
        return dto;
    }
    @Test
    void shouldCreateEvento() throws Exception{
        EventoRequestDTO evento = buildEventoRequestDto();
        mockMvc.perform(post("/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(evento)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.name").value("Juan"))
                .andExpect(jsonPath("$.attendees").value(50));
    }
    @Test
    void shouldFindEventoById() throws Exception {

        EventoRequestDTO dto = buildEventoRequestDto();

        String response = mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        EventoResponseDTO saved =
                objectMapper.readValue(response, EventoResponseDTO.class);

        mockMvc.perform(get("/eventos/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }
    @Test
    void shouldReturnAllEventos() throws Exception {

        EventoRequestDTO dto = buildEventoRequestDto();

        mockMvc.perform(post("/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(get("/eventos")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty());
    }

    @Test
    void shouldUpdateEventoStatus() throws Exception {

        EventoRequestDTO dto = buildEventoRequestDto();

        String response = mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        EventoResponseDTO saved =
                objectMapper.readValue(response, EventoResponseDTO.class);

        EventoStatusUpdateDTO updateDTO =
                new EventoStatusUpdateDTO();

        updateDTO.setStatus("PENDIENTE");
        updateDTO.setReason("Evento aprobado");

        mockMvc.perform(patch("/eventos/" + saved.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("PENDIENTE"));
    }
    @Test
    void shouldReturn404WhenEventoNotFound() throws Exception {
        mockMvc.perform(get("/eventos/999999")).andExpect(status().isNotFound());
    }
    @Test
    void shouldRejectInvalidEmail() throws Exception {
        EventoRequestDTO dto = buildEventoRequestDto();
        dto.setEmail("invalid-email");

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email")
                        .exists());
    }
    @Test
    void shouldRejectBlankName() throws Exception {
        EventoRequestDTO dto = buildEventoRequestDto();
        dto.setName("");

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name")
                        .exists());
    }
    @Test
    void shouldRejectPastDate() throws Exception {
        EventoRequestDTO dto = buildEventoRequestDto();
        dto.setDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.date")
                        .exists());
    }
    @Test
    void shouldRejectTooManyAttendees() throws Exception {
        EventoRequestDTO dto = buildEventoRequestDto();
        dto.setAttendees(999);

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.attendees")
                        .exists());
    }
    @Test
    void shouldRejectWhenPrivacyNotAccepted() throws Exception {
        EventoRequestDTO dto = buildEventoRequestDto();
        dto.setPrivacyAccepted(false);

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.privacyAccepted")
                        .exists());
    }
    @Test
    void shouldRejectWhenAgeNotConfirmed() throws Exception {
        EventoRequestDTO dto = buildEventoRequestDto();
        dto.setAgeConfirmed(false);

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ageConfirmed")
                        .exists());
    }
    @Test
    void shouldRejectShortComments() throws Exception {
        EventoRequestDTO dto = buildEventoRequestDto();
        dto.setComments("short");

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.comments")
                        .exists());
    }
    @Test
    void shouldRejectInvalidPhone() throws Exception {
        EventoRequestDTO dto = buildEventoRequestDto();
        dto.setPhone("abc123");

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.phone")
                        .exists());
    }
}