package com.microservicio.Producto;

import com.microservicio.Producto.Services.ProductoServiceRead;
import com.microservicio.Producto.Services.ProductoServiceWrite;
import com.microservicio.Producto.dto.ProductoDTO;
import com.microservicio.Producto.Entities.Categoria;
import com.microservicio.Producto.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class MicroservicioProductoApplicationTests {
	@Autowired
	private ProductoServiceRead productoRead;
	@Autowired
	private ProductoServiceWrite productoWrite;

	@Test
	void contextLoads() {
	}

	private ProductoDTO buildProductoDTO() {
		ProductoDTO dto = new ProductoDTO();
		dto.setNombre("Pizza Test");
		dto.setDescripcion("Pizza familiar");
		dto.setPrecio(BigDecimal.valueOf(45.50));
		dto.setCategoria(Categoria.PLATO);
		dto.setStock(20);
		return dto;
	}
	//Please work I beg u
	private MockMultipartFile buildImage() {
		return new MockMultipartFile(
				"imagen",
				"test.jpg",
				"image/jpeg",
				"fake-image-content".getBytes()
		);
	}
	@Test
	void shouldSaveProducto() {
		ProductoDTO dto = buildProductoDTO();
		ProductoDTO saved = productoWrite.save(dto, buildImage());
		Assertions.assertThat(saved).isNotNull();
		Assertions.assertThat(saved.getProductoid()).isNotNull();
		Assertions.assertThat(saved.getNombre())
				.isEqualTo("Pizza Test");
	}

	@Test
	void shouldFindProductoById() {
		ProductoDTO dto = buildProductoDTO();
		ProductoDTO saved = productoWrite.save(dto, buildImage());
		Optional<ProductoDTO> found = productoRead.findById(saved.getProductoid());
		Assertions.assertThat(found).isPresent();
		Assertions.assertThat(found.get().getNombre()).isEqualTo("Pizza Test");
	}

	@Test
	void shouldReturnAllProductos() {
		ProductoDTO dto = buildProductoDTO();
		productoWrite.save(dto, buildImage());
		List<ProductoDTO> result = productoRead.findAll();
		Assertions.assertThat(result).isNotEmpty();
	}

	@Test
	void shouldFindByCategoria() {
		ProductoDTO dto = buildProductoDTO();
		productoWrite.save(dto, buildImage());
		List<ProductoDTO> result = productoRead.findByCategoria(Categoria.PLATO);
		Assertions.assertThat(result).isNotEmpty();
	}

	@Test
	void shouldFindByPrecioRange() {
		ProductoDTO dto = buildProductoDTO();
		productoWrite.save(dto, buildImage());
		List<ProductoDTO> result = productoRead.findByPrecioRange(40.0, 50.0);
		Assertions.assertThat(result).isNotEmpty();
	}

	@Test
	void shouldUpdateProducto() {
		ProductoDTO dto = buildProductoDTO();
		ProductoDTO saved = productoWrite.save(dto, buildImage());
		ProductoDTO updatedDto = buildProductoDTO();
		updatedDto.setNombre("Hamburguesa");
		updatedDto.setPrecio(BigDecimal.valueOf(30));
		ProductoDTO updated = productoWrite.update(
						saved.getProductoid(),
						updatedDto,
						buildImage()
				);
		Assertions.assertThat(updated.getNombre()).isEqualTo("Hamburguesa");
		Assertions.assertThat(updated.getPrecio()).isEqualByComparingTo(BigDecimal.valueOf(30));
	}

	@Test
	void shouldDeleteProducto() {
		ProductoDTO dto = buildProductoDTO();
		ProductoDTO saved = productoWrite.save(dto, buildImage());
		productoWrite.delete(saved.getProductoid());
		Optional<ProductoDTO> found = productoRead.findById(saved.getProductoid());
		Assertions.assertThat(found).isEmpty();
	}

	@Test
	void shouldUpdateStock() {
		ProductoDTO dto = buildProductoDTO();
		ProductoDTO saved = productoWrite.save(dto, buildImage());
		ProductoDTO updated = productoWrite.updateStock(saved.getProductoid(), 100 );
		Assertions.assertThat(updated.getStock()).isEqualTo(100);
	}

	@Test
	void shouldUpdateImagen() {
		ProductoDTO dto = buildProductoDTO();
		ProductoDTO saved = productoWrite.save(dto, buildImage());
		MockMultipartFile newImage = new MockMultipartFile(
						"imagen",
						"new.jpg",
						"image/jpeg",
						"new-image".getBytes()
				);
		productoWrite.updateImagen(saved.getProductoid(), newImage );
		Optional<ProductoDTO> updated = productoRead.findById(saved.getProductoid());
		Assertions.assertThat(updated).isPresent();
		Assertions.assertThat(updated.get().getImagenProducto()).isNotNull();
	}

	@Test
	void shouldDeleteImagen() {
		ProductoDTO dto = buildProductoDTO();
		ProductoDTO saved = productoWrite.save(dto, buildImage());
		productoWrite.deleteImagen(saved.getProductoid());
		Optional<ProductoDTO> updated = productoRead.findById(saved.getProductoid());
		Assertions.assertThat(updated).isPresent();
		Assertions.assertThat(updated.get().getImagenProducto()).isNull();
	}

	@Test
	void shouldThrowWhenProductoNotFound() {

		Assertions.assertThatThrownBy(() ->
						productoWrite.delete(99999))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void shouldThrowWhenStockIsNegative() {
		Assertions.assertThatThrownBy(() ->
						productoWrite.updateStock(11, -50))
				.isInstanceOf(IllegalArgumentException.class);
	}
}