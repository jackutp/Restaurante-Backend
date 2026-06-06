package com.microservicio.reservas;

import com.microservicio.reservas.dto.CrearReservaRequestDTO;
import com.microservicio.reservas.dto.ReservaResponseDTO;
import com.microservicio.reservas.entity.EstadoReserva;
import com.microservicio.reservas.entity.Reserva;
import com.microservicio.reservas.exception.ResourceNotFoundException;
import com.microservicio.reservas.repository.ReservaRepository;
import com.microservicio.reservas.service.ReservaService;
import com.microservicio.reservas.service.TokenService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("tests")
@Transactional
class MicroservicioReservasApplicationTests {
	@Autowired
	private ReservaService reservaService;
	@Autowired
	private ReservaRepository reservaRepository;
	@Autowired
	private TokenService tokenService;
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
	void crearReserva_ShouldSaveReserva() {
		ReservaResponseDTO response = reservaService.crearReserva(request);
		assertNotNull(response);
		assertNotNull(response.id());
		assertNotNull(response.codigo());
		assertEquals("Juan", response.nombre());
		assertEquals("Perez", response.apellido());
		assertEquals("juan@test.com", response.email());
		assertEquals(EstadoReserva.PENDIENTE, response.estado());
		Reserva saved = reservaRepository.findById(response.id()).orElseThrow();
		assertEquals("Juan", saved.getNombre());
		assertEquals("2026-05-30", saved.getFecha().toString());
		assertEquals("20:00", saved.getHora().toString());
	}

	@Test
	void crearReserva_ShouldGenerateCodigo() {
		ReservaResponseDTO response = reservaService.crearReserva(request);
		assertNotNull(response.codigo());
		assertFalse(response.codigo().isBlank());
	}

	@Test
	void listarTodas_ShouldReturnReservas() {
		reservaService.crearReserva(request);
		List<ReservaResponseDTO> reservas = reservaService.listarTodas();
		assertEquals(5, reservas.size());
	}

	@Test
	void listarPorEstado_ShouldReturnReservas() {
		reservaService.crearReserva(request);
		List<ReservaResponseDTO> reservas = reservaService.listarPorEstado(EstadoReserva.PENDIENTE);
		assertEquals(3, reservas.size());
		assertEquals(EstadoReserva.PENDIENTE, reservas.get(0).estado());
	}

	@Test
	void listarPorFecha_ShouldReturnReservas() {
		reservaService.crearReserva(request);
		List<ReservaResponseDTO> reservas = reservaService.listarPorFecha(LocalDate.of(2026, 5, 30));
		assertEquals(1, reservas.size());
	}

	@Test
	void obtenerPorId_ShouldReturnReserva() {
		ReservaResponseDTO created = reservaService.crearReserva(request);
		ReservaResponseDTO found = reservaService.obtenerPorId(created.id());
		assertEquals(created.id(), found.id());
		assertEquals(created.codigo(), found.codigo());
	}

	@Test
	void obtenerPorId_ShouldThrowResourceNotFound() {
		assertThrows(ResourceNotFoundException.class,
				() -> reservaService.obtenerPorId(999L)
		);
	}

	@Test
	void obtenerPorCodigo_ShouldReturnReserva() {
		ReservaResponseDTO created = reservaService.crearReserva(request);
		ReservaResponseDTO found = reservaService.obtenerPorCodigo(created.codigo());
		assertEquals(created.codigo(), found.codigo());
	}

	@Test
	void obtenerPorCodigo_ShouldThrowResourceNotFound() {
		assertThrows(ResourceNotFoundException.class,
				() -> reservaService.obtenerPorCodigo("INVALIDO")
		);
	}

	@Test
	void actualizarEstado_ShouldUpdateEstado() {
		ReservaResponseDTO created = reservaService.crearReserva(request);
		ReservaResponseDTO updated = reservaService.actualizarEstado(
						created.id(),
						EstadoReserva.COMPLETADA
				);

		assertEquals(EstadoReserva.COMPLETADA, updated.estado());

		Reserva saved =
				reservaRepository.findById(created.id()).orElseThrow();

		assertEquals(EstadoReserva.COMPLETADA, saved.getEstado());

		assertNotNull(saved.getUpdatedAt());
	}

	@Test
	void actualizarEstado_ShouldThrowResourceNotFound() {

		assertThrows(
				ResourceNotFoundException.class,
				() -> reservaService.actualizarEstado(
						999L,
						EstadoReserva.PENDIENTE
				)
		);
	}

	@Test
	void listarReservasDelDia_ShouldReturnTodayReservas() {

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

		reservaService.crearReserva(todayRequest);

		List<ReservaResponseDTO> reservas =
				reservaService.listarReservasDelDia();

		assertEquals(1, reservas.size());
	}
}
