package com.microservicio.eventos.Exceptions;
public class EventoException extends RuntimeException {
    public EventoException(String message) {
        super(message);
    }
}