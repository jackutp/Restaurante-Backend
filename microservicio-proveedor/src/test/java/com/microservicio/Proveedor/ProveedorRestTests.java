package com.microservicio.Proveedor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicio.Proveedor.Entities.OrdenCompra;
import com.microservicio.Proveedor.Entities.Proveedor;
import com.microservicio.Proveedor.Repositories.OrdenCompraRepository;
import com.microservicio.Proveedor.Repositories.ProveedorRepository;
import com.microservicio.Proveedor.dto.ProveedorRequestDTO;
import com.microservicio.Proveedor.Entities.EstadoOrden;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("tests")
@Transactional
public class ProveedorRestTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    private ProveedorRequestDTO buildProveedorDTO() {
        ProveedorRequestDTO dto = new ProveedorRequestDTO();
        dto.setNombre("Proveedor Test");
        dto.setDescripcion("Proveedor de prueba");
        dto.setRuc("12345678901");
        dto.setRazonSocial("Proveedor SAC");
        dto.setDireccionFiscal("Av Peru 123");
        return dto;
    }

    private Proveedor createProveedor() {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Proveedor Test");
        proveedor.setDescripcion("Proveedor");
        proveedor.setRuc("12345678901");
        proveedor.setRazonSocial("Proveedor SAC");
        proveedor.setDireccionFiscal("Av Peru 123");
        return proveedorRepository.save(proveedor);
    }

    private OrdenCompra createOrden(Proveedor proveedor) {
        OrdenCompra orden = new OrdenCompra();
        orden.setProveedor(proveedor);
        orden.setEstado(EstadoOrden.PENDIENTE);
        return ordenCompraRepository.save(orden);
    }

    @Test
    void shouldGetAllProveedores() throws Exception {
        createProveedor();
        mockMvc.perform(get("/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetProveedorById() throws Exception {
        Proveedor proveedor = createProveedor();
        mockMvc.perform(get("/proveedores/{id}", proveedor.getProveedorid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proveedor Test"));
    }

    @Test
    void shouldReturn404WhenProveedorNotFound() throws Exception {
        mockMvc.perform(get("/proveedores/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateProveedor() throws Exception {
        ProveedorRequestDTO dto = buildProveedorDTO();
        mockMvc.perform(post("/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.proveedorid").exists())
                .andExpect(jsonPath("$.nombre").value("Proveedor Test"));
    }

    @Test
    void shouldUpdateProveedor() throws Exception {
        Proveedor proveedor = createProveedor();
        ProveedorRequestDTO dto = buildProveedorDTO();
        dto.setNombre("Proveedor Actualizado");

        mockMvc.perform(put("/proveedores/{id}", proveedor.getProveedorid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proveedor Actualizado"));
    }

    @Test
    void shouldDeleteProveedor() throws Exception {
        Proveedor proveedor = createProveedor();
        mockMvc.perform(delete("/proveedores/{id}",
                        proveedor.getProveedorid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Proveedor eliminado exitosamente"));
    }

    // Ordenes

    @Test
    void shouldGetAllOrdenes() throws Exception {
        Proveedor proveedor = createProveedor();
        createOrden(proveedor);
        mockMvc.perform(get("/proveedores/ordenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetOrdenById() throws Exception {
        Proveedor proveedor = createProveedor();
        OrdenCompra orden = createOrden(proveedor);
        mockMvc.perform(get("/proveedores/ordenes/{id}",
                        orden.getOrdenId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordenId").value(orden.getOrdenId()));
    }

    @Test
    void shouldReturn404WhenOrdenNotFound() throws Exception {
        mockMvc.perform(get("/proveedores/ordenes/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetOrdenesByProveedor() throws Exception {
        Proveedor proveedor = createProveedor();
        createOrden(proveedor);
        mockMvc.perform(get("/proveedores/{id}/ordenes",
                        proveedor.getProveedorid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldCreateOrden() throws Exception {
        Proveedor proveedor = createProveedor();
        Map<String, Integer> request = new HashMap<>();
        request.put("proveedorId", proveedor.getProveedorid());
        mockMvc.perform(post("/proveedores/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ordenId").exists())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void shouldReturn400WhenProveedorIdMissing() throws Exception {
        Map<String, Integer> request = new HashMap<>();
        mockMvc.perform(post("/proveedores/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldUpdateEstadoOrden() throws Exception {
        Proveedor proveedor = createProveedor();
        OrdenCompra orden = createOrden(proveedor);
        Map<String, String> request = new HashMap<>();
        request.put("estado", "RECIBIDO");
        mockMvc.perform(patch("/proveedores/ordenes/{id}/estado",
                        orden.getOrdenId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECIBIDO"));
    }

    @Test
    void shouldReturn400WhenEstadoIsInvalid() throws Exception {
        Proveedor proveedor = createProveedor();
        OrdenCompra orden = createOrden(proveedor);
        Map<String, String> request = new HashMap<>();
        request.put("estado", "INVALIDO");
        mockMvc.perform(patch("/proveedores/ordenes/{id}/estado",
                        orden.getOrdenId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUploadFactura() throws Exception {
        Proveedor proveedor = createProveedor();
        OrdenCompra orden = createOrden(proveedor);
        MockMultipartFile factura = new MockMultipartFile(
                "factura",
                "factura.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "contenido pdf".getBytes()
        );

        mockMvc.perform(multipart("/proveedores/ordenes/{id}/factura",
                        orden.getOrdenId())
                        .file(factura))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facturaNombre").value("factura.pdf"));
    }

    @Test
    void shouldDownloadFactura() throws Exception {
        Proveedor proveedor = createProveedor();
        OrdenCompra orden = createOrden(proveedor);
        orden.setFacturaNombre("factura.pdf");
        orden.setFacturaTipo(MediaType.APPLICATION_PDF_VALUE);
        orden.setFacturaContenido("contenido".getBytes());
        ordenCompraRepository.save(orden);
        mockMvc.perform(get("/proveedores/ordenes/{id}/factura",
                        orden.getOrdenId()))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Disposition",
                        Matchers.containsString("factura.pdf")
                ));
    }

    @Test
    void shouldDeleteFactura() throws Exception {
        Proveedor proveedor = createProveedor();
        OrdenCompra orden = createOrden(proveedor);
        orden.setFacturaNombre("factura.pdf");
        orden.setFacturaTipo(MediaType.APPLICATION_PDF_VALUE);
        orden.setFacturaContenido("contenido".getBytes());
        ordenCompraRepository.save(orden);
        mockMvc.perform(delete("/proveedores/ordenes/{id}/factura",
                        orden.getOrdenId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Factura eliminada exitosamente"));
    }

    @Test
    void shouldDeleteOrden() throws Exception {
        Proveedor proveedor = createProveedor();
        OrdenCompra orden = createOrden(proveedor);
        mockMvc.perform(delete("/proveedores/ordenes/{id}",
                        orden.getOrdenId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Orden eliminada exitosamente"));
    }
}
