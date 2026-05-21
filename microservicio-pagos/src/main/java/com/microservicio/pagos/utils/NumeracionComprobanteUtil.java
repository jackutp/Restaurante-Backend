package com.microservicio.pagos.utils;

import com.microservicio.pagos.repository.ComprobanteRepository;
import org.springframework.stereotype.Component;

@Component
public class NumeracionComprobanteUtil {

    private final ComprobanteRepository comprobanteRepository;

    public NumeracionComprobanteUtil(ComprobanteRepository comprobanteRepository) {
        this.comprobanteRepository = comprobanteRepository;
    }

    public String generarNumeroComprobante(String tipo) {
        String serie = tipo.equals("BOLETA") ? "B001" : "F001";
        Integer ultimoCorrelativo = comprobanteRepository.findMaxCorrelativoBySerie(serie);
        int nuevoCorrelativo = (ultimoCorrelativo == null ? 0 : ultimoCorrelativo) + 1;
        return String.format("%s-%08d", serie, nuevoCorrelativo);
    }

    public Integer obtenerCorrelativo(String serie) {
        Integer ultimo = comprobanteRepository.findMaxCorrelativoBySerie(serie);
        return (ultimo == null ? 0 : ultimo) + 1;
    }
}