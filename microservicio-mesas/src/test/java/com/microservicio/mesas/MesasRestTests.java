package com.microservicio.mesas;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.mesas.dto.ActualizarEstadoMesaRequestDTO;
import com.microservicio.mesas.dto.ActualizarTotalMesaRequestDTO;
import com.microservicio.mesas.dto.CrearMesaRequestDTO;
import com.microservicio.mesas.entity.EstadoMesa;
import com.microservicio.mesas.entity.Mesa;
import com.microservicio.mesas.repository.MesaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("tests")
@Transactional
public class MesasRestTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MesaRepository mesaRepository;

    private CrearMesaRequestDTO crearMesaRequest;

    @BeforeEach
    void setUp() {
        crearMesaRequest = new CrearMesaRequestDTO();
        crearMesaRequest.setNumero(100);
        crearMesaRequest.setCapacidad(4);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void createMesa_ShouldCreateMesa() throws Exception {
        mockMvc.perform(post("/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearMesaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value(100))
                .andExpect(jsonPath("$.capacidad").value(4))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
        Optional<Mesa> savedMesa = mesaRepository.findByNumero(100);
        assertTrue(savedMesa.isPresent());
    }

    @Test
    void createMesa_ShouldReturnConflict_WhenNumeroExists() throws Exception {
        mesaRepository.save(
                new Mesa(
                        100,
                        4,
                        EstadoMesa.DISPONIBLE
                )
        );
        mockMvc.perform(post("/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearMesaRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ya existe una mesa con el número: 100"));
    }

    @Test
    void createMesa_ShouldReturnBadRequest_WhenNumeroIsInvalid() throws Exception {
        CrearMesaRequestDTO request = new CrearMesaRequestDTO();
        request.setNumero(0);
        request.setCapacidad(4);
        mockMvc.perform(post("/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.numero").value("El número debe ser mayor a 0"));
    }

    @Test
    void getAllMesas_ShouldReturnAllMesas() throws Exception {
        mesaRepository.save(new Mesa(4, 4, EstadoMesa.DISPONIBLE));
        mesaRepository.save(new Mesa(5, 6, EstadoMesa.OCUPADO));
        mockMvc.perform(get("/mesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void getMesasByEstado_ShouldReturnFilteredMesas() throws Exception {
        mesaRepository.save(new Mesa(10, 4, EstadoMesa.DISPONIBLE));
        mesaRepository.save(new Mesa(11, 4, EstadoMesa.OCUPADO));

        mockMvc.perform(get("/mesas/estado/DISPONIBLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("DISPONIBLE"));
    }

    @Test
    void getMesaById_ShouldReturnMesa() throws Exception {
        Mesa mesa = mesaRepository.save(new Mesa(20, 4, EstadoMesa.DISPONIBLE));
        mockMvc.perform(get("/mesas/" + mesa.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(20))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
    }

    @Test
    void getMesaById_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/mesas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Mesa no encontrada con ID: 999"));
    }

    @Test
    void getMesaByNumero_ShouldReturnMesa() throws Exception {
        mesaRepository.save(new Mesa(30, 4, EstadoMesa.DISPONIBLE));
        mockMvc.perform(get("/mesas/numero/30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(30));
    }

    @Test
    void getMesaByNumero_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/mesas/numero/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Mesa no encontrada con número: 999"));
    }

    @Test
    void updateEstado_ShouldUpdateMesaEstado() throws Exception {
        Mesa mesa = mesaRepository.save(new Mesa(40, 4, EstadoMesa.DISPONIBLE));
        ActualizarEstadoMesaRequestDTO request = new ActualizarEstadoMesaRequestDTO();
        request.setEstado(EstadoMesa.OCUPADO);
        request.setTotalActual(150.0);
        request.setOrdenActualId("ORD-100");
        mockMvc.perform(put("/mesas/" + mesa.getId() + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("OCUPADO"))
                .andExpect(jsonPath("$.totalActual").value(150.0))
                .andExpect(jsonPath("$.ordenActualId").value("ORD-100"));
    }

    @Test
    void updateEstado_ShouldResetValues_WhenDisponible() throws Exception {
        Mesa mesa = new Mesa(
                50,
                4,
                EstadoMesa.OCUPADO
        );
        mesa.setTotalActual(500.0);
        mesa.setOrdenActualId("ORD-500");
        mesa = mesaRepository.save(mesa);
        ActualizarEstadoMesaRequestDTO request = new ActualizarEstadoMesaRequestDTO();
        request.setEstado(EstadoMesa.DISPONIBLE);
        mockMvc.perform(put("/mesas/" + mesa.getId() + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"))
                .andExpect(jsonPath("$.totalActual").value(0.0))
                .andExpect(jsonPath("$.ordenActualId").doesNotExist());
    }

    @Test
    void updateTotal_ShouldUpdateTotal() throws Exception {
        Mesa mesa = mesaRepository.save(new Mesa(60, 4, EstadoMesa.OCUPADO));
        ActualizarTotalMesaRequestDTO request = new ActualizarTotalMesaRequestDTO();

        request.setTotal(250.0);

        mockMvc.perform(put("/mesas/" + mesa.getId() + "/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActual").value(250.0));
    }

    @Test
    void deleteMesa_ShouldDeleteMesa() throws Exception {
        Mesa mesa = mesaRepository.save(new Mesa(70, 4, EstadoMesa.DISPONIBLE));
        mockMvc.perform(delete("/mesas/" + mesa.getId()))
                .andExpect(status().isNoContent());

        Optional<Mesa> deleted = mesaRepository.findById(mesa.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void deleteMesa_ShouldReturnConflict_WhenMesaOccupied() throws Exception {
        Mesa mesa = mesaRepository.save(new Mesa(80, 4, EstadoMesa.OCUPADO));
        mockMvc.perform(delete("/mesas/" + mesa.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("No se puede eliminar una mesa ocupada o reservada"));
    }

    @Test
    void updateMesa_ShouldUpdateMesa() throws Exception {
        Mesa mesa = mesaRepository.save(new Mesa(90, 4, EstadoMesa.DISPONIBLE));
        CrearMesaRequestDTO request = new CrearMesaRequestDTO();
        request.setNumero(91);
        request.setCapacidad(8);
        mockMvc.perform(put("/mesas/" + mesa.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(91))
                .andExpect(jsonPath("$.capacidad").value(8));
    }

    @Test
    void updateMesa_ShouldReturnConflict_WhenMesaOccupied() throws Exception {
        Mesa mesa = mesaRepository.save(new Mesa(95, 4, EstadoMesa.OCUPADO));
        CrearMesaRequestDTO request = new CrearMesaRequestDTO();

        request.setNumero(96);
        request.setCapacidad(8);

        mockMvc.perform(put("/mesas/" + mesa.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("No se puede editar una mesa ocupada o reservada"));
    }

    @Test
    void updateEstadoByNumero_ShouldAccumulateTotal() throws Exception {
        Mesa mesa = new Mesa(200, 4, EstadoMesa.OCUPADO);
        mesa.setTotalActual(100.0);
        mesaRepository.save(mesa);
        ActualizarEstadoMesaRequestDTO request = new ActualizarEstadoMesaRequestDTO();
        request.setEstado(EstadoMesa.OCUPADO);
        request.setTotalActual(50.0);
        request.setOrdenActualId("ORD-900");
        mockMvc.perform(put("/mesas/numero/200/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActual").value(150.0));
    }

    @Test
    void updateTotalByNumero_ShouldAccumulateTotal() throws Exception {
        Mesa mesa = new Mesa(
                300,
                4,
                EstadoMesa.OCUPADO
        );
        mesa.setTotalActual(200.0);
        mesaRepository.save(mesa);
        ActualizarTotalMesaRequestDTO request = new ActualizarTotalMesaRequestDTO();
        request.setTotal(75.0);
        mockMvc.perform(put("/mesas/numero/300/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActual").value(275.0));
    }
}
