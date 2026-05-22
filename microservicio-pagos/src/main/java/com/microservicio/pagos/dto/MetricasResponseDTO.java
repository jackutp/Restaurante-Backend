package com.microservicio.pagos.dto;

import java.util.List;
import java.util.Map;

public class MetricasResponseDTO {

    // Tarjetas
    private Double ventasDelDia;
    private Long ordenesCompletadas;
    private Double ticketPromedio;
    private Integer mesasOcupadas;
    private Integer totalMesas;
    private Double ocupacionPorcentaje;

    // Gráficos
    private List<Double> ventasUltimos7Dias;
    private List<String> diasSemana;
    private List<ProductoTopDTO> productosTop;
    private List<VentaPorHoraDTO> ventasPorHora;
    private Map<String, Long> ordenesPorEstado;

    // Getters y Setters
    public Double getVentasDelDia() { return ventasDelDia; }
    public void setVentasDelDia(Double ventasDelDia) { this.ventasDelDia = ventasDelDia; }
    public Long getOrdenesCompletadas() { return ordenesCompletadas; }
    public void setOrdenesCompletadas(Long ordenesCompletadas) { this.ordenesCompletadas = ordenesCompletadas; }
    public Double getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(Double ticketPromedio) { this.ticketPromedio = ticketPromedio; }
    public Integer getMesasOcupadas() { return mesasOcupadas; }
    public void setMesasOcupadas(Integer mesasOcupadas) { this.mesasOcupadas = mesasOcupadas; }
    public Integer getTotalMesas() { return totalMesas; }
    public void setTotalMesas(Integer totalMesas) { this.totalMesas = totalMesas; }
    public Double getOcupacionPorcentaje() { return ocupacionPorcentaje; }
    public void setOcupacionPorcentaje(Double ocupacionPorcentaje) { this.ocupacionPorcentaje = ocupacionPorcentaje; }
    public List<Double> getVentasUltimos7Dias() { return ventasUltimos7Dias; }
    public void setVentasUltimos7Dias(List<Double> ventasUltimos7Dias) { this.ventasUltimos7Dias = ventasUltimos7Dias; }
    public List<String> getDiasSemana() { return diasSemana; }
    public void setDiasSemana(List<String> diasSemana) { this.diasSemana = diasSemana; }
    public List<ProductoTopDTO> getProductosTop() { return productosTop; }
    public void setProductosTop(List<ProductoTopDTO> productosTop) { this.productosTop = productosTop; }
    public List<VentaPorHoraDTO> getVentasPorHora() { return ventasPorHora; }
    public void setVentasPorHora(List<VentaPorHoraDTO> ventasPorHora) { this.ventasPorHora = ventasPorHora; }
    public Map<String, Long> getOrdenesPorEstado() { return ordenesPorEstado; }
    public void setOrdenesPorEstado(Map<String, Long> ordenesPorEstado) { this.ordenesPorEstado = ordenesPorEstado; }

    // Clases anidadas
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

    public static class VentaPorHoraDTO {
        private Integer hora;
        private Double total;

        public VentaPorHoraDTO(Integer hora, Double total) {
            this.hora = hora;
            this.total = total;
        }

        public Integer getHora() { return hora; }
        public void setHora(Integer hora) { this.hora = hora; }
        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
    }
}