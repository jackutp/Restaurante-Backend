package com.microservicio.pedidos.dto;

import java.util.List;
import java.util.Map;

public class MetricasPedidosResponseDTO {

    private Long ordenesCompletadas;
    private Map<String, Long> ordenesPorEstado;
    private List<ProductoTopDTO> productosTop;

    public Long getOrdenesCompletadas() { return ordenesCompletadas; }
    public void setOrdenesCompletadas(Long ordenesCompletadas) { this.ordenesCompletadas = ordenesCompletadas; }
    public Map<String, Long> getOrdenesPorEstado() { return ordenesPorEstado; }
    public void setOrdenesPorEstado(Map<String, Long> ordenesPorEstado) { this.ordenesPorEstado = ordenesPorEstado; }
    public List<ProductoTopDTO> getProductosTop() { return productosTop; }
    public void setProductosTop(List<ProductoTopDTO> productosTop) { this.productosTop = productosTop; }

    public static class ProductoTopDTO {
        private String nombre;
        private Long cantidad;
        private Double total;

        public ProductoTopDTO(String nombre, Long cantidad, Double total) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.total = total;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public Long getCantidad() { return cantidad; }
        public void setCantidad(Long cantidad) { this.cantidad = cantidad; }
        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
    }
}