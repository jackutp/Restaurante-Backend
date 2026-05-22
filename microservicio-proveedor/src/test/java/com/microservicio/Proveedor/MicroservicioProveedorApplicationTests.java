package com.microservicio.Proveedor;

import com.microservicio.Proveedor.Entities.EstadoOrden;
import com.microservicio.Proveedor.Services.orden_compra.OrdenCompraReadService;
import com.microservicio.Proveedor.Services.orden_compra.OrdenCompraWriteService;
import com.microservicio.Proveedor.Services.proveedor.ProveedorServiceRead;
import com.microservicio.Proveedor.Services.proveedor.ProveedorServiceWrite;
import com.microservicio.Proveedor.dto.OrdenCompraDTO;
import com.microservicio.Proveedor.dto.OrdenCompraRequestDTO;
import com.microservicio.Proveedor.dto.ProveedorDTO;
import com.microservicio.Proveedor.dto.ProveedorRequestDTO;
import com.microservicio.Proveedor.exception.FileStorageException;
import com.microservicio.Proveedor.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class MicroservicioProveedorApplicationTests {
	@Autowired
	private OrdenCompraReadService ordenRead;
	@Autowired
	private OrdenCompraWriteService ordenWrite;
	@Autowired
	private ProveedorServiceRead proveedorRead;
	@Autowired
	private ProveedorServiceWrite proveedorWrite;

	private ProveedorRequestDTO buildProveedorDTO() {
		ProveedorRequestDTO dto = new ProveedorRequestDTO();
		dto.setNombre("Proveedor Test");
		dto.setDescripcion("Proveedor de prueba");
		dto.setRuc("12345678901");
		dto.setRazonSocial("Proveedor SAC");
		dto.setDireccionFiscal("Av. Siempre Viva 123");
		return dto;
	}

	private OrdenCompraRequestDTO buildOrdenDTO(Integer proveedorId) {
		return new OrdenCompraRequestDTO(
				proveedorId,
				List.of(1, 2)
		);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void shouldSaveProveedor() {
		ProveedorRequestDTO dto = buildProveedorDTO();
		ProveedorDTO saved = proveedorWrite.save(dto);
		Assertions.assertThat(saved).isNotNull();
		Assertions.assertThat(saved.getProveedorid()).isNotNull();
		Assertions.assertThat(saved.getNombre()).isEqualTo("Proveedor Test");
		Assertions.assertThat(saved.getRuc()).isEqualTo("12345678901");
	}

	@Test
	void shouldFindProveedorById() {
		ProveedorDTO saved = proveedorWrite.save(buildProveedorDTO());
		Optional<ProveedorDTO> found = proveedorRead.findById(saved.getProveedorid());
		Assertions.assertThat(found).isPresent();
		Assertions.assertThat(found.get().getNombre()).isEqualTo("Proveedor Test");
	}

	@Test
	void shouldReturnAllProveedores() {
		proveedorWrite.save(buildProveedorDTO());
		List<ProveedorDTO> proveedores = proveedorRead.findAll();
		Assertions.assertThat(proveedores).isNotEmpty();
	}

	@Test
	void shouldUpdateProveedor() {
		ProveedorDTO saved = proveedorWrite.save(buildProveedorDTO());
		ProveedorRequestDTO updatedDto = buildProveedorDTO();
		updatedDto.setNombre("Proveedor Actualizado");
		updatedDto.setDescripcion("Nueva descripcion");
		ProveedorDTO updated = proveedorWrite.update(saved.getProveedorid(), updatedDto);
		Assertions.assertThat(updated.getNombre()).isEqualTo("Proveedor Actualizado");

		Assertions.assertThat(updated.getDescripcion()).isEqualTo("Nueva descripcion");
	}

	@Test
	void shouldDeleteProveedor() {
		ProveedorDTO saved = proveedorWrite.save(buildProveedorDTO());
		proveedorWrite.delete(saved.getProveedorid());
		Optional<ProveedorDTO> found = proveedorRead.findById(saved.getProveedorid());
		Assertions.assertThat(found).isEmpty();
	}

	@Test
	void shouldThrowWhenUpdatingNonExistingProveedor() {
		ProveedorRequestDTO dto = buildProveedorDTO();
		Assertions.assertThatThrownBy(() -> proveedorWrite.update(99999, dto)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldThrowWhenDeletingNonExistingProveedor() {
		Assertions.assertThatThrownBy(() -> proveedorWrite.delete(99999)).isInstanceOf(ResourceNotFoundException.class);
	}

	//OrdenCompra
	@Test
	void shouldCreateOrdenCompra() {
		ProveedorDTO proveedor = proveedorWrite.save(buildProveedorDTO());
		OrdenCompraRequestDTO dto = buildOrdenDTO(proveedor.getProveedorid());
		OrdenCompraDTO saved = ordenWrite.create(dto);
		Assertions.assertThat(saved).isNotNull();
		Assertions.assertThat(saved.getOrdenId()).isNotNull();
		Assertions.assertThat(saved.getProveedorId()).isEqualTo(proveedor.getProveedorid());
		Assertions.assertThat(saved.getEstado()).isEqualTo(EstadoOrden.PENDIENTE);
	}

	@Test
	void shouldFindOrdenById() {
		ProveedorDTO proveedor = proveedorWrite.save(buildProveedorDTO());
		OrdenCompraDTO saved = ordenWrite.create(buildOrdenDTO(proveedor.getProveedorid()));
		Optional<OrdenCompraDTO> found = ordenRead.findById(saved.getOrdenId());
		Assertions.assertThat(found).isPresent();
		Assertions.assertThat(found.get().getOrdenId()).isEqualTo(saved.getOrdenId());
	}

	@Test
	void shouldReturnAllOrdenes() {
		ProveedorDTO proveedor = proveedorWrite.save(buildProveedorDTO());
		ordenWrite.create(buildOrdenDTO(proveedor.getProveedorid()));
		List<OrdenCompraDTO> ordenes = ordenRead.findAll();
		Assertions.assertThat(ordenes).isNotEmpty();
	}

	@Test
	void shouldFindOrdenesByProveedor() {
		ProveedorDTO proveedor = proveedorWrite.save(buildProveedorDTO());
		ordenWrite.create(buildOrdenDTO(proveedor.getProveedorid()));
		List<OrdenCompraDTO> ordenes = ordenRead.findByProveedor(proveedor.getProveedorid());
		Assertions.assertThat(ordenes).isNotEmpty();
		Assertions.assertThat(ordenes.get(0).getProveedorId()).isEqualTo(proveedor.getProveedorid());
	}

	@Test
	void shouldUpdateEstadoOrden() {
		ProveedorDTO proveedor = proveedorWrite.save(buildProveedorDTO());
		OrdenCompraDTO saved = ordenWrite.create(buildOrdenDTO(proveedor.getProveedorid()));
		OrdenCompraDTO updated = ordenWrite.updateEstado(
						saved.getOrdenId(),
						EstadoOrden.RECIBIDO
				);

		Assertions.assertThat(updated.getEstado())
				.isEqualTo(EstadoOrden.RECIBIDO);
	}

	@Test
	void shouldDeleteOrdenCompra() {
		ProveedorDTO proveedor = proveedorWrite.save(buildProveedorDTO());
		OrdenCompraDTO saved = ordenWrite.create(buildOrdenDTO(proveedor.getProveedorid()));
		ordenWrite.delete(saved.getOrdenId());
		Optional<OrdenCompraDTO> found = ordenRead.findById(saved.getOrdenId());
		Assertions.assertThat(found).isEmpty();
	}

	@Test
	void shouldThrowWhenProveedorDoesNotExist() {
		OrdenCompraRequestDTO dto = buildOrdenDTO(99999);
		Assertions.assertThatThrownBy(() ->
						ordenWrite.create(dto))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldThrowWhenUpdatingEstadoOfNonExistingOrden() {
		Assertions.assertThatThrownBy(() ->
						ordenWrite.updateEstado(
								99999,
								EstadoOrden.RECIBIDO
						))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldThrowWhenDeletingNonExistingOrden() {
		Assertions.assertThatThrownBy(() ->
						ordenWrite.delete(99999))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldThrowWhenDownloadingFacturaWithoutFactura() {
		ProveedorDTO proveedor = proveedorWrite.save(buildProveedorDTO());
		OrdenCompraDTO saved = ordenWrite.create(buildOrdenDTO(proveedor.getProveedorid()));
		Assertions.assertThatThrownBy(() ->
						ordenRead.descargarFactura(saved.getOrdenId()))
				.isInstanceOf(FileStorageException.class);
	}

}
