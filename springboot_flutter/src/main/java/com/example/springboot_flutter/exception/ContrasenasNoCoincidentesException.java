package com.example.springboot_flutter.exception;

/**
 * ContrasenasNoCoincidentesException - Excepción cuando la contraseña no coincide con su confirmación
 * HTTP Status: 400 BAD REQUEST
 */
public class ContrasenasNoCoincidentesException extends RuntimeException {

    public ContrasenasNoCoincidentesException() {
        super("Las contraseñas no coinciden");
    }

    public ContrasenasNoCoincidentesException(String mensaje) {
        super(mensaje);
    }

    public ContrasenasNoCoincidentesException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }
}
