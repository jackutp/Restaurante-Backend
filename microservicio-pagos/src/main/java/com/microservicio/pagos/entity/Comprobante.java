package com.microservicio.pagos.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "comprobantes")
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo", nullable = false)
    private String tipo; // BOLETA, FACTURA

    @Column(name = "serie", nullable = false)
    private String serie; // B001, F001

    @Column(name = "correlativo", nullable = false)
    private Integer correlativo;

    @Column(name = "numero_completo", nullable = false, unique = true)
    private String numeroCompleto; // B001-00000001

    @Column(name = "ruc")
    private String ruc; // Solo para factura

    @Column(name = "razon_social")
    private String razonSocial; // Solo para factura

    @Column(name = "orden_id", nullable = false)
    private String ordenId;

    @Column(name = "mesa_numero", nullable = false)
    private Integer mesaNumero;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "created_at")
    private String createdAt;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }
    public Integer getCorrelativo() { return correlativo; }
    public void setCorrelativo(Integer correlativo) { this.correlativo = correlativo; }
    public String getNumeroCompleto() { return numeroCompleto; }
    public void setNumeroCompleto(String numeroCompleto) { this.numeroCompleto = numeroCompleto; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }
    public Integer getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(Integer mesaNumero) { this.mesaNumero = mesaNumero; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}