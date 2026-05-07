package service.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class resourceNotFoundException extends RuntimeException {

    public resourceNotFoundException() {
        super("Recurso no encontrado");
    }

    public resourceNotFoundException(String message) {
        super(message);
    }

    // Este constructor es el que más vas a usar
    public resourceNotFoundException(String recurso, Integer id) {
        super(recurso + " no encontrado con id: " + id);
    }

    public resourceNotFoundException(String recurso, String campo, String valor) {
        super(recurso + " no encontrado con " + campo + ": " + valor);
    }
}