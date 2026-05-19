package com.example.springboot_flutter.exception;

/**
 * CredencialesInvalidasException - Excepción cuando las credenciales son inválidas
 * HTTP Status: 401 UNAUTHORIZED
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }

    public CredencialesInvalidasException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }
}
