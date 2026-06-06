package com.microservicio.Producto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.Producto.dto.ProductoDTO;
import com.microservicio.Producto.Entities.Categoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("tests")
@Transactional
public class ProductoRestTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    //Please work I beg u - Part 2: Electric Boogalo
    private MockMultipartFile buildImage() {
        return new MockMultipartFile(
                "imagen",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );
    }
    @Test
    void shouldCreateProducto() throws Exception {
        MockMultipartFile image = buildImage();
        mockMvc.perform(multipart("/productos/crear")
                                .file(image)
                                .param("nombre", "Pizza Test")
                                .param("descripcion", "Pizza familiar")
                                .param("precio", "45.50")
                                .param("categoria", Categoria.PLATO.name())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productoid").exists())
                .andExpect(jsonPath("$.nombre").value("Pizza Test"))
                .andExpect(jsonPath("$.precio").value(45.50));
    }

    @Test
    void shouldFindProductoById() throws Exception {
        MockMultipartFile image = buildImage();
        String response = mockMvc.perform(
                        multipart("/productos/crear")
                                .file(image)
                                .param("nombre", "Hamburguesa")
                                .param("descripcion", "Doble carne")
                                .param("precio", "30")
                                .param("categoria", Categoria.PLATO.name())
                )
                .andReturn()
                .getResponse()
                .getContentAsString();
        ProductoDTO saved = objectMapper.readValue(response, ProductoDTO.class);
        mockMvc.perform(get("/productos/" + saved.getProductoid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre")
                        .value("Hamburguesa"));
    }

    @Test
    void shouldReturnAllProductos() throws Exception {
        MockMultipartFile image = buildImage();
        mockMvc.perform(
                multipart("/productos")
                        .file(image)
                        .param("nombre", "Producto Test")
                        .param("descripcion", "Descripcion")
                        .param("precio", "20")
                        .param("categoria", Categoria.PLATO.name())
        );
        mockMvc.perform(get("/productos/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldFindByCategoria() throws Exception {
        MockMultipartFile image = buildImage();
        mockMvc.perform(
                multipart("/productos")
                        .file(image)
                        .param("nombre", "Producto Categoria")
                        .param("descripcion", "Descripcion")
                        .param("precio", "25")
                        .param("categoria", Categoria.PLATO.name())
        );
        mockMvc.perform(get("/productos/categoria/" + Categoria.PLATO.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldFindByPrecioRange() throws Exception {
        MockMultipartFile image = buildImage();
        mockMvc.perform(
                multipart("/productos")
                        .file(image)
                        .param("nombre", "Producto Precio")
                        .param("descripcion", "Descripcion")
                        .param("precio", "40")
                        .param("categoria", Categoria.PLATO.name())
        );
        mockMvc.perform(
                        get("/productos/precio")
                                .param("min", "30")
                                .param("max", "50")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldUpdateProducto() throws Exception {
        MockMultipartFile image = buildImage();
        String response = mockMvc.perform(
                        multipart("/productos/crear")
                                .file(image)
                                .param("nombre", "Original")
                                .param("descripcion", "Original Desc")
                                .param("precio", "50")
                                .param("categoria", Categoria.PLATO.name())
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProductoDTO saved = objectMapper.readValue(response, ProductoDTO.class);

        MockMultipartFile newImage =
                new MockMultipartFile(
                        "imagen",
                        "updated.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "updated-image".getBytes()
                );

        mockMvc.perform(
                        multipart("/productos/" + saved.getProductoid())
                                .file(newImage)
                                .param("nombre", "Actualizado")
                                .param("descripcion", "Nueva descripcion")
                                .param("precio", "60")
                                .param("categoria", Categoria.PLATO.name())
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre")
                        .value("Actualizado"));
    }

    @Test
    void shouldUpdateStock() throws Exception {
        MockMultipartFile image = buildImage();
        String response = mockMvc.perform(
                        multipart("/productos/crear")
                                .file(image)
                                .param("nombre", "Stock Test")
                                .param("descripcion", "Descripcion")
                                .param("precio", "20")
                                .param("categoria", Categoria.PLATO.name())
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProductoDTO saved = objectMapper.readValue(response, ProductoDTO.class);
        mockMvc.perform(
                        put("/productos/" + saved.getProductoid() + "/stock")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "stock": 100
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock")
                        .value(100));
    }

    @Test
    void shouldDeleteProducto() throws Exception {
        MockMultipartFile image = buildImage();
        String response = mockMvc.perform(
                        multipart("/productos/crear")
                                .file(image)
                                .param("nombre", "Eliminar")
                                .param("descripcion", "Descripcion")
                                .param("precio", "10")
                                .param("categoria", Categoria.PLATO.name())
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProductoDTO saved = objectMapper.readValue(response, ProductoDTO.class);

        mockMvc.perform(delete("/productos/" + saved.getProductoid())).andExpect(status().isOk());
    }

    @Test
    void shouldUpdateImagen() throws Exception {
        MockMultipartFile image = buildImage();
        String response = mockMvc.perform(
                        multipart("/productos/crear")
                                .file(image)
                                .param("nombre", "Imagen")
                                .param("descripcion", "Descripcion")
                                .param("precio", "20")
                                .param("categoria", Categoria.PLATO.name())
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProductoDTO saved = objectMapper.readValue(response, ProductoDTO.class);

        MockMultipartFile newImage = new MockMultipartFile(
                        "imagen",
                        "new.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "new-image".getBytes()
                );

        mockMvc.perform(
                        multipart("/productos/" + saved.getProductoid() + "/imagen")
                                .file(newImage)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteImagen() throws Exception {
        MockMultipartFile image = buildImage();
        String response = mockMvc.perform(
                        multipart("/productos/crear")
                                .file(image)
                                .param("nombre", "Imagen Delete")
                                .param("descripcion", "Descripcion")
                                .param("precio", "20")
                                .param("categoria", Categoria.PLATO.name())
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProductoDTO saved = objectMapper.readValue(response, ProductoDTO.class);
        mockMvc.perform(delete("/productos/" + saved.getProductoid() + "/imagen")).andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenProductoNotFound() throws Exception {
        mockMvc.perform(get("/productos/99999")).andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectNegativeStock() throws Exception {
        mockMvc.perform(
                        put("/productos/10/stock")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "stock": -10
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }
}
