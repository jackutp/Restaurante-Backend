package com.microservicio.mesas;

import com.microservicio.mesas.dto.ActualizarEstadoMesaRequestDTO;
import com.microservicio.mesas.dto.ActualizarTotalMesaRequestDTO;
import com.microservicio.mesas.dto.CrearMesaRequestDTO;
import com.microservicio.mesas.dto.MesaResponseDTO;
import com.microservicio.mesas.entity.EstadoMesa;
import com.microservicio.mesas.entity.Mesa;
import com.microservicio.mesas.exception.ConflictException;
import com.microservicio.mesas.exception.ResourceNotFoundException;
import com.microservicio.mesas.repository.MesaRepository;
import com.microservicio.mesas.service.MesaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class MicroservicioMesasApplicationTests {
	@Autowired
	private MesaService mesaService;

	@Autowired
	private MesaRepository mesaRepository;

	private CrearMesaRequestDTO crearMesaRequest;

	@BeforeEach
	void setUp() {
		crearMesaRequest = new CrearMesaRequestDTO();
		crearMesaRequest.setNumero(20);
		crearMesaRequest.setCapacidad(4);
	}

	@Test
	void contextLoads() {
	}
	@Test
	void createMesa_ShouldCreateMesa() {
		MesaResponseDTO response = mesaService.createMesa(crearMesaRequest);
		assertNotNull(response);
		assertNotNull(response.getId());
		assertEquals(20, response.getNumero());
		assertEquals(4, response.getCapacidad());
		assertEquals(EstadoMesa.DISPONIBLE, response.getEstado());
		Optional<Mesa> savedMesa = mesaRepository.findByNumero(1);
		assertTrue(savedMesa.isPresent());
	}

	@Test
	void createMesa_ShouldThrowConflict_WhenNumeroExists() {
		Mesa mesa = new Mesa(
				20,
				4,
				EstadoMesa.DISPONIBLE
		);

		mesaRepository.save(mesa);
		ConflictException exception = assertThrows(
				ConflictException.class,
				() -> mesaService.createMesa(crearMesaRequest)
		);
		assertEquals(
				"Ya existe una mesa con el número: 20",
				exception.getMessage()
		);
	}

	@Test
	void getAllMesas_ShouldReturnAllMesas() {
		mesaRepository.save(new Mesa(
				30,
				4,
				EstadoMesa.DISPONIBLE
		));
		mesaRepository.save(new Mesa(
				40,
				6,
				EstadoMesa.OCUPADO
		));

		List<MesaResponseDTO> mesas =
				mesaService.getAllMesas();
		assertEquals(5, mesas.size());
	}

	@Test
	void getMesasByEstado_ShouldReturnFilteredMesas() {

		mesaRepository.save(new Mesa(
				40,
				4,
				EstadoMesa.DISPONIBLE
		));

		mesaRepository.save(new Mesa(
				41,
				6,
				EstadoMesa.OCUPADO
		));

		List<MesaResponseDTO> mesas =
				mesaService.getMesasByEstado(
						EstadoMesa.DISPONIBLE
				);

		assertEquals(2, mesas.size());
		assertEquals(
				EstadoMesa.DISPONIBLE,
				mesas.get(0).getEstado()
		);
	}

	@Test
	void getMesaById_ShouldReturnMesa() {

		Mesa mesa = mesaRepository.save(
				new Mesa(
						5,
						4,
						EstadoMesa.DISPONIBLE
				)
		);

		MesaResponseDTO response =
				mesaService.getMesaById(mesa.getId());

		assertEquals(5, response.getNumero());
	}

	@Test
	void getMesaById_ShouldThrowNotFound() {
		ResourceNotFoundException exception = assertThrows(
				ResourceNotFoundException.class,
				() -> mesaService.getMesaById(999L)
		);

		assertEquals(
				"Mesa no encontrada con ID: 999",
				exception.getMessage()
		);
	}

	@Test
	void getMesaByNumero_ShouldReturnMesa() {
		mesaRepository.save(
				new Mesa(
						8,
						4,
						EstadoMesa.DISPONIBLE
				)
		);
		MesaResponseDTO response =
				mesaService.getMesaByNumero(8);
		assertEquals(8, response.getNumero());
	}

	@Test
	void getMesaByNumero_ShouldThrowNotFound() {

		ResourceNotFoundException exception = assertThrows(
				ResourceNotFoundException.class,
				() -> mesaService.getMesaByNumero(99)
		);
		assertEquals(
				"Mesa no encontrada con número: 99",
				exception.getMessage()
		);
	}

	@Test
	void updateEstado_ShouldUpdateMesaEstado() {
		Mesa mesa = mesaRepository.save(
				new Mesa(
						45,
						4,
						EstadoMesa.DISPONIBLE
				)
		);
		ActualizarEstadoMesaRequestDTO request = new ActualizarEstadoMesaRequestDTO();
		request.setEstado(EstadoMesa.OCUPADO);
		request.setTotalActual(150.0);
		request.setOrdenActualId("ORD-100");
		MesaResponseDTO response =
				mesaService.updateEstado(
						mesa.getId(),
						request
				);

		assertEquals(
				EstadoMesa.OCUPADO,
				response.getEstado()
		);

		assertEquals(150.0, response.getTotalActual());
		assertEquals("ORD-100", response.getOrdenActualId());
	}

	@Test
	void updateEstado_ShouldResetValues_WhenDisponible() {

		Mesa mesa = new Mesa(
				4,
				4,
				EstadoMesa.OCUPADO
		);

		mesa.setTotalActual(300.0);
		mesa.setOrdenActualId("ORD-200");

		mesa = mesaRepository.save(mesa);

		ActualizarEstadoMesaRequestDTO request =
				new ActualizarEstadoMesaRequestDTO();

		request.setEstado(EstadoMesa.DISPONIBLE);

		MesaResponseDTO response =
				mesaService.updateEstado(
						mesa.getId(),
						request
				);

		assertEquals(
				EstadoMesa.DISPONIBLE,
				response.getEstado()
		);

		assertEquals(0.0, response.getTotalActual());
		assertNull(response.getOrdenActualId());
	}

	@Test
	void updateTotal_ShouldUpdateMesaTotal() {

		Mesa mesa = mesaRepository.save(
				new Mesa(
						6,
						4,
						EstadoMesa.OCUPADO
				)
		);
		ActualizarTotalMesaRequestDTO request = new ActualizarTotalMesaRequestDTO();

		request.setTotal(250.0);

		MesaResponseDTO response =
				mesaService.updateTotal(
						mesa.getId(),
						request
				);

		assertEquals(250.0, response.getTotalActual());
	}

	@Test
	void deleteMesa_ShouldDeleteMesa() {

		Mesa mesa = mesaRepository.save(
				new Mesa(
						7,
						4,
						EstadoMesa.DISPONIBLE
				)
		);

		mesaService.deleteMesa(mesa.getId());

		Optional<Mesa> deleted =
				mesaRepository.findById(mesa.getId());

		assertFalse(deleted.isPresent());
	}

	@Test
	void deleteMesa_ShouldThrowConflict_WhenMesaNotDisponible() {

		Mesa mesa = mesaRepository.save(
				new Mesa(
						7,
						4,
						EstadoMesa.OCUPADO
				)
		);

		ConflictException exception = assertThrows(
				ConflictException.class,
				() -> mesaService.deleteMesa(mesa.getId())
		);

		assertEquals(
				"No se puede eliminar una mesa ocupada o reservada",
				exception.getMessage()
		);
	}

	@Test
	void updateMesa_ShouldUpdateMesa() {

		Mesa mesa = mesaRepository.save(
				new Mesa(
						10,
						4,
						EstadoMesa.DISPONIBLE
				)
		);

		CrearMesaRequestDTO request =
				new CrearMesaRequestDTO();

		request.setNumero(20);
		request.setCapacidad(8);

		MesaResponseDTO response =
				mesaService.updateMesa(
						mesa.getId(),
						request
				);

		assertEquals(20, response.getNumero());
		assertEquals(8, response.getCapacidad());
	}

	@Test
	void updateMesa_ShouldThrowConflict_WhenMesaOccupied() {

		Mesa mesa = mesaRepository.save(
				new Mesa(
						11,
						4,
						EstadoMesa.OCUPADO
				)
		);

		CrearMesaRequestDTO request =
				new CrearMesaRequestDTO();

		request.setNumero(30);
		request.setCapacidad(6);

		ConflictException exception = assertThrows(
				ConflictException.class,
				() -> mesaService.updateMesa(
						mesa.getId(),
						request
				)
		);

		assertEquals(
				"No se puede editar una mesa ocupada o reservada",
				exception.getMessage()
		);
	}

	@Test
	void updateEstadoByNumero_ShouldAccumulateTotal() {

		Mesa mesa = new Mesa(
				15,
				4,
				EstadoMesa.OCUPADO
		);

		mesa.setTotalActual(100.0);

		mesaRepository.save(mesa);

		ActualizarEstadoMesaRequestDTO request =
				new ActualizarEstadoMesaRequestDTO();

		request.setEstado(EstadoMesa.OCUPADO);
		request.setTotalActual(50.0);
		request.setOrdenActualId("ORD-500");

		MesaResponseDTO response =
				mesaService.updateEstadoByNumero(
						15,
						request
				);

		assertEquals(150.0, response.getTotalActual());
	}

	@Test
	void updateTotalByNumero_ShouldAccumulateTotal() {

		Mesa mesa = new Mesa(
				20,
				4,
				EstadoMesa.OCUPADO
		);

		mesa.setTotalActual(200.0);

		mesaRepository.save(mesa);

		ActualizarTotalMesaRequestDTO request =
				new ActualizarTotalMesaRequestDTO();

		request.setTotal(75.0);

		MesaResponseDTO response =
				mesaService.updateTotalByNumero(
						20,
						request
				);

		assertEquals(275.0, response.getTotalActual());
	}
}
