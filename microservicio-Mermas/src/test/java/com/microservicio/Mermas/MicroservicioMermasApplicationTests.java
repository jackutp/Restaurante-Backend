package com.microservicio.Mermas;

import com.microservicio.Mermas.Entities.TipoMerma;
import com.microservicio.Mermas.Services.MermaServiceRead;
import com.microservicio.Mermas.Services.MermaServiceWrite;
import com.microservicio.Mermas.Services.feign.InsumoFeignClient;
import com.microservicio.Mermas.Services.feign.ProductoFeignClient;
import com.microservicio.Mermas.dto.InsumoDTO;
import com.microservicio.Mermas.dto.MermaDTO;
import com.microservicio.Mermas.dto.MermaRequestDTO;
import com.microservicio.Mermas.dto.ProductoDTO;
import com.microservicio.Mermas.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class MicroservicioMermasApplicationTests {

	@Autowired
	private MermaServiceWrite mermaWrite;
	@Autowired
	private MermaServiceRead mermaRead;

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
	void contextLoads() {
	}
	@Test
	void shouldSaveMerma() {
		MermaRequestDTO dto = buildRequestDTO();
		MermaDTO saved = mermaWrite.save(dto);
		Assertions.assertThat(saved).isNotNull();
		Assertions.assertThat(saved.getMermaid()).isNotNull();
		Assertions.assertThat(saved.getNombreMerma()).isEqualTo("Harina vencida");
		Assertions.assertThat(saved.getCantidad()).isEqualTo("5");
	}

	@Test
	void shouldFindAllMermas() {
		MermaRequestDTO dto = buildRequestDTO();
		mermaWrite.save(dto);
		List<MermaDTO> result = mermaRead.findAll();
		Assertions.assertThat(result).isNotEmpty();
	}

	@Test
	void shouldFindByTipo() {
		MermaRequestDTO dto = buildRequestDTO();
		mermaWrite.save(dto);
		List<MermaDTO> result = mermaRead.findByTipo(TipoMerma.INSUMO);
		Assertions.assertThat(result).isNotEmpty();
		Assertions.assertThat(result.get(0).getTipoMerma()).isEqualTo(TipoMerma.INSUMO);
	}

	@Test
	void shouldUpdateMerma() {
		MermaRequestDTO dto = buildRequestDTO();
		MermaDTO saved = mermaWrite.save(dto);
		MermaRequestDTO updatedDto = buildRequestDTO();
		updatedDto.setNombreMerma("Aceite derramado");
		updatedDto.setCantidad("10");
		updatedDto.setMotivo("Daño durante transporte");
		MermaDTO updated = mermaWrite.update(saved.getMermaid(), updatedDto);
		Assertions.assertThat(updated.getNombreMerma()).isEqualTo("Aceite derramado");
		Assertions.assertThat(updated.getCantidad()).isEqualTo("10");
		Assertions.assertThat(updated.getMotivo()).isEqualTo("Daño durante transporte");
	}

	@Test
	void shouldDeleteMerma() {
		MermaRequestDTO dto = buildRequestDTO();
		MermaDTO saved = mermaWrite.save(dto);
		mermaWrite.delete(saved.getMermaid());
		Assertions.assertThatThrownBy(() -> mermaRead.findById(saved.getMermaid())).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldThrowWhenUpdatingNonExistingMerma() {
		MermaRequestDTO dto = buildRequestDTO();
		Assertions.assertThatThrownBy(() -> mermaWrite.update(99999, dto)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldThrowWhenDeletingNonExistingMerma() {
		Assertions.assertThatThrownBy(() -> mermaWrite.delete(99999)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldGetProductosFromFeignClient() {
		List<ProductoDTO> productos = List.of(
				new ProductoDTO()
		);
		Mockito.when(productoFeignClient.getAllProductos()).thenReturn(productos);
		List<ProductoDTO> result = mermaRead.getProductos();
		Assertions.assertThat(result).hasSize(1);
	}

	@Test
	void shouldGetInsumosFromFeignClient() {
		List<InsumoDTO> insumos = List.of(
				new InsumoDTO()
		);
		Mockito.when(insumoFeignClient.getAllInsumos()).thenReturn(insumos);
		List<InsumoDTO> result = mermaRead.getInsumos();
		Assertions.assertThat(result).hasSize(1);
	}
}
