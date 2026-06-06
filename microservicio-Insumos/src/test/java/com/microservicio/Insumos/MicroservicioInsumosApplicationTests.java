package com.microservicio.Insumos;

import com.microservicio.Insumos.Services.InsumoServiceRead;
import com.microservicio.Insumos.Services.InsumoServiceWrite;
import com.microservicio.Insumos.dto.InsumoDTO;
import com.microservicio.Insumos.dto.InsumoRequestDTO;
import com.microservicio.Insumos.Entities.EstadoInsumo;
import com.microservicio.Insumos.Entities.UnidadMedida;
import com.microservicio.Insumos.exception.ConflictException;
import com.microservicio.Insumos.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("tests")
@Transactional
class MicroservicioInsumosApplicationTests {

	@Autowired
	private  InsumoServiceRead insumoRead;
	@Autowired
	private  InsumoServiceWrite insumoWrite;

	private InsumoRequestDTO buildRequestDTO(){
		InsumoRequestDTO dto = new InsumoRequestDTO();
		dto.setNombre("Insumo Genérico 3");
		dto.setUnidadMedida(UnidadMedida.L);
		dto.setStock(50);
		dto.setEstadoInsumo(EstadoInsumo.DISPONIBLE);
		return dto;
	}
	@Test
	void contextLoads() {
	}
	@Test
	void shouldSaveInsumo() {
		InsumoRequestDTO dto = buildRequestDTO();
		InsumoDTO saved = insumoWrite.save(dto);

		Assertions.assertThat(saved).isNotNull();
		Assertions.assertThat(saved.getInsumoid()).isNotNull();
		Assertions.assertThat(saved.getNombre()).isEqualTo("Insumo Genérico 3");
		Assertions.assertThat(saved.getStock()).isEqualTo(50);
	}

	@Test
	void shouldFindById() {
		InsumoRequestDTO dto = buildRequestDTO();
		InsumoDTO saved = insumoWrite.save(dto);
		Optional<InsumoDTO> found = insumoRead.findById(saved.getInsumoid());
		Assertions.assertThat(found).isPresent();
		Assertions.assertThat(found.get().getNombre()).isEqualTo("Insumo Genérico 3");
	}

	@Test
	void shouldReturnAllInsumos() {
		InsumoRequestDTO dto = buildRequestDTO();
		insumoWrite.save(dto);
		List<InsumoDTO> result = insumoRead.findAll();
		Assertions.assertThat(result).isNotEmpty();
	}

	@Test
	void shouldFindByEstado() {
		InsumoRequestDTO dto = buildRequestDTO();
		insumoWrite.save(dto);
		List<InsumoDTO> result = insumoRead.findByEstado(EstadoInsumo.DISPONIBLE);
		Assertions.assertThat(result).isNotEmpty();
	}

	@Test
	void shouldFindByNombre() {
		InsumoRequestDTO dto = buildRequestDTO();
		insumoWrite.save(dto);
		List<InsumoDTO> result = insumoRead.findByNombre("Insumo Genérico 3");
		Assertions.assertThat(result).isNotEmpty();
	}

	@Test
	void shouldUpdateInsumo() {
		InsumoRequestDTO dto = buildRequestDTO();
		InsumoDTO saved = insumoWrite.save(dto);
		InsumoRequestDTO updatedDto = buildRequestDTO();
		updatedDto.setNombre("Azucar");
		updatedDto.setStock(100);
		InsumoDTO updated = insumoWrite.update(saved.getInsumoid(), updatedDto);
		Assertions.assertThat(updated.getNombre()).isEqualTo("Azucar");
		Assertions.assertThat(updated.getStock()).isEqualTo(100);
	}

	@Test
	void shouldDeleteInsumo() {
		InsumoRequestDTO dto = buildRequestDTO();
		InsumoDTO saved = insumoWrite.save(dto);
		insumoWrite.delete(saved.getInsumoid());
		Optional<InsumoDTO> found = insumoRead.findById(saved.getInsumoid());
		Assertions.assertThat(found).isEmpty();
	}

	@Test
	void shouldUpdateStock() {
		InsumoRequestDTO dto = buildRequestDTO();
		InsumoDTO saved = insumoWrite.save(dto);
		InsumoDTO updated = insumoWrite.updateStock(saved.getInsumoid(), 200);
		Assertions.assertThat(updated.getStock()).isEqualTo(200);
	}

	@Test
	void shouldThrowWhenDuplicateName() {
		InsumoRequestDTO dto = buildRequestDTO();
		insumoWrite.save(dto);
		Assertions.assertThatThrownBy(() -> insumoWrite.save(dto))
				.isInstanceOf(ConflictException.class);
	}

	@Test
	void shouldThrowWhenUpdatingNonExistingInsumo() {
		InsumoRequestDTO dto = buildRequestDTO();
		Assertions.assertThatThrownBy(() -> insumoWrite.update(99999, dto))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldThrowWhenDeletingNonExistingInsumo() {

		Assertions.assertThatThrownBy(() -> insumoWrite.delete(99999))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldThrowWhenStockIsNegative() {

		Assertions.assertThatThrownBy(() -> insumoWrite.updateStock(1, -50))
				.isInstanceOf(IllegalArgumentException.class);
	}
}
