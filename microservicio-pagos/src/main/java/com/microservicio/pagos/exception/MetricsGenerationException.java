package com.microservicio.pagos.exception;

public class MetricsGenerationException extends RuntimeException {
    public MetricsGenerationException(String message) {
        super(message);
    }
  public MetricsGenerationException(String message, Throwable cause) {
    super(message, cause);
  }
}
