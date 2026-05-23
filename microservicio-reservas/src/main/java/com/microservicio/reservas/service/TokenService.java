package com.microservicio.reservas.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class TokenService {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public String generarCodigoReserva() {
        // Formato: RES-20251225-A1B2C3
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = Integer.toHexString(timestamp.hashCode()).toUpperCase();
        return "RES-" + hash.substring(0, Math.min(8, hash.length()));
    }
}