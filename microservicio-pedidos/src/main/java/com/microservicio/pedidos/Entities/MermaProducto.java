package com.microservicio.pedidos.Entities;

import java.time.LocalDate;

public class MermaProducto {
    private Long MermaProductoId;
    private Long DetallePedidoId;
    private String motivo;
    private LocalDate fecha;
    public MermaProducto(Long detallePedidoId, LocalDate fecha, Long mermaProductoId, String motivo) {
        DetallePedidoId = detallePedidoId;
        this.fecha = fecha;
        MermaProductoId = mermaProductoId;
        this.motivo = motivo;
    }
    public Long getDetallePedidoId() {return DetallePedidoId;}
    public void setDetallePedidoId(Long detallePedidoId) {DetallePedidoId = detallePedidoId;}
    public LocalDate getFecha() {return fecha;}
    public void setFecha(LocalDate fecha) {this.fecha = fecha;}
    public Long getMermaProductoId() {return MermaProductoId;}
    public void setMermaProductoId(Long mermaProductoId) {MermaProductoId = mermaProductoId;}
    public String getMotivo() {return motivo;}
    public void setMotivo(String motivo) {this.motivo = motivo;}
}
