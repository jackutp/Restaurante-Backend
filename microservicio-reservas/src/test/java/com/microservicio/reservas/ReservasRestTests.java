package com.microservicio.reservas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.reservas.dto.CrearReservaRequestDTO;
import com.microservicio.reservas.dto.ReservaResponseDTO;
import com.microservicio.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("tests")
@Transactional
public class ReservasRestTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ReservaRepository reservaRepository;
    private CrearReservaRequestDTO request;

    @BeforeEach
    void setUp() {

        request = new CrearReservaRequestDTO(
                "Juan",
                "Perez",
                "juan@test.com",
                "999888777",
                "2026-05-30",
                "20:00",
                4,
                "Cena",
                "Ninguna",
                "Mesa ventana",
                "Ninguna"
        );
    }

    @Test
    void contextLoads() {
    }
    @Test
    void crearReserva_ShouldReturnCreated() throws Exception {

        mockMvc.perform(post("/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.codigo").isNotEmpty())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"))
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void crearReserva_ShouldReturnBadRequest_WhenEmailInvalid() throws Exception {

        CrearReservaRequestDTO invalidRequest =
                new CrearReservaRequestDTO(
                        "Juan",
                        "Perez",
                        "correo-invalido",
                        "999888777",
                        "2026-05-30",
                        "20:00",
                        4,
                        "Cena",
                        null,
                        null,
                        null
                );

        mockMvc.perform(post("/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearReserva_ShouldReturnBadRequest_WhenFechaInvalid() throws Exception {

        CrearReservaRequestDTO invalidRequest =
                new CrearReservaRequestDTO(
                        "Juan",
                        "Perez",
                        "juan@test.com",
                        "999888777",
                        "30-05-2026",
                        "20:00",
                        4,
                        "Cena",
                        null,
                        null,
                        null
                );

        mockMvc.perform(post("/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearReserva_ShouldReturnBadRequest_WhenHoraInvalid() throws Exception {

        CrearReservaRequestDTO invalidRequest =
                new CrearReservaRequestDTO(
                        "Juan",
                        "Perez",
                        "juan@test.com",
                        "999888777",
                        "2026-05-30",
                        "99:99",
                        4,
                        "Cena",
                        null,
                        null,
                        null
                );

        mockMvc.perform(post("/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearReserva_ShouldReturnBadRequest_WhenPersonasInvalid() throws Exception {

        CrearReservaRequestDTO invalidRequest =
                new CrearReservaRequestDTO(
                        "Juan",
                        "Perez",
                        "juan@test.com",
                        "999888777",
                        "2026-05-30",
                        "20:00",
                        0,
                        "Cena",
                        null,
                        null,
                        null
                );

        mockMvc.perform(post("/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTodas_ShouldReturnReservas() throws Exception {

        mockMvc.perform(post("/reservas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/reservas/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listarPorEstado_ShouldReturnReservas() throws Exception {

        mockMvc.perform(post("/reservas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/reservas/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void listarPorFecha_ShouldReturnReservas() throws Exception {

        mockMvc.perform(post("/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/reservas/fecha/2026-05-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listarReservasDelDia_ShouldReturnReservas() throws Exception {

        CrearReservaRequestDTO todayRequest =
                new CrearReservaRequestDTO(
                        "Maria",
                        "Lopez",
                        "maria@test.com",
                        "999111222",
                        LocalDate.now().toString(),
                        "21:00",
                        2,
                        "Cena",
                        null,
                        null,
                        null
                );

        mockMvc.perform(post("/reservas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todayRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/reservas/dia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Maria"));
    }

    @Test
    void obtenerPorId_ShouldReturnReserva() throws Exception {

        String response =
                mockMvc.perform(post("/reservas/crear")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        ReservaResponseDTO created =
                objectMapper.readValue(response, ReservaResponseDTO.class);

        mockMvc.perform(get("/reservas/" + created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.id()))
                .andExpect(jsonPath("$.codigo").value(created.codigo()));
    }

    @Test
    void obtenerPorId_ShouldReturnNotFound() throws Exception {

        mockMvc.perform(get("/reservas/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorCodigo_ShouldReturnReserva() throws Exception {

        String response =
                mockMvc.perform(post("/reservas/crear")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        ReservaResponseDTO created =
                objectMapper.readValue(response, ReservaResponseDTO.class);

        mockMvc.perform(get("/reservas/codigo/" + created.codigo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(created.codigo()));
    }

    @Test
    void obtenerPorCodigo_ShouldReturnNotFound() throws Exception {

        mockMvc.perform(get("/reservas/codigo/INVALIDO"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarEstado_ShouldUpdateEstado() throws Exception {

        String response =
                mockMvc.perform(post("/reservas/crear")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        ReservaResponseDTO created =
                objectMapper.readValue(response, ReservaResponseDTO.class);

        mockMvc.perform(patch("/reservas/" + created.id() + "/estado")
                        .param("estado", "COMPLETADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    void actualizarEstado_ShouldReturnNotFound() throws Exception {

        mockMvc.perform(patch("/reservas/99999/estado")
                        .param("estado", "COMPLETADA"))
                .andExpect(status().isNotFound());
    }
}
