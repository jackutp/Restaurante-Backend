package com.microservicio.inventario.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "mermainsumos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MermaInsumos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merma_insumo_id")
    private Long mermaInsumoId;

    @ManyToOne
    @JoinColumn(name = "ingrediente_fk", nullable = false)
    private Ingrediente ingrediente;

    @Column(name = "motivo", columnDefinition = "TEXT", nullable = false)
    private String motivo;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @PrePersist
    protected void onCreate() {
        fecha = LocalDate.now();
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public Long getMermaInsumoId() {
        return mermaInsumoId;
    }

    public void setMermaInsumoId(Long mermaInsumoId) {
        this.mermaInsumoId = mermaInsumoId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}