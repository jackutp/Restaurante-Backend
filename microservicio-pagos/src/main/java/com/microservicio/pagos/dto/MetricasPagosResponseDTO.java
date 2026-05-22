package com.microservicio.pagos.dto;

import java.util.List;

public class MetricasPagosResponseDTO {

    private Double ventasDelDia;
    private List<Double> ventasUltimos7Dias;
    private List<String> diasSemana;
    private List<VentaPorHoraDTO> ventasPorHora;
    private Integer mesasOcupadas;
    private Integer totalMesas;
    private Double ocupacionPorcentaje;

    // Getters y Setters
    public Double getVentasDelDia() { return ventasDelDia; }
    public void setVentasDelDia(Double ventasDelDia) { this.ventasDelDia = ventasDelDia; }
    public List<Double> getVentasUltimos7Dias() { return ventasUltimos7Dias; }
    public void setVentasUltimos7Dias(List<Double> ventasUltimos7Dias) { this.ventasUltimos7Dias = ventasUltimos7Dias; }
    public List<String> getDiasSemana() { return diasSemana; }
    public void setDiasSemana(List<String> diasSemana) { this.diasSemana = diasSemana; }
    public List<VentaPorHoraDTO> getVentasPorHora() { return ventasPorHora; }
    public void setVentasPorHora(List<VentaPorHoraDTO> ventasPorHora) { this.ventasPorHora = ventasPorHora; }
    public Integer getMesasOcupadas() { return mesasOcupadas; }
    public void setMesasOcupadas(Integer mesasOcupadas) { this.mesasOcupadas = mesasOcupadas; }
    public Integer getTotalMesas() { return totalMesas; }
    public void setTotalMesas(Integer totalMesas) { this.totalMesas = totalMesas; }
    public Double getOcupacionPorcentaje() { return ocupacionPorcentaje; }
    public void setOcupacionPorcentaje(Double ocupacionPorcentaje) { this.ocupacionPorcentaje = ocupacionPorcentaje; }

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