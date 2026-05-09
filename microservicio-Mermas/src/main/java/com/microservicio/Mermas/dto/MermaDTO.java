package com.microservicio.Mermas.dto;

import com.microservicio.Mermas.Entities.TipoMerma;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MermaDTO {
    private Integer mermaid;
    private TipoMerma tipoMerma;
    private String nombreMerma;
    private String cantidad;
    private String motivo;
    private LocalDateTime fecha;
    private Integer referenciaId;
    private String unidadMedida;
}