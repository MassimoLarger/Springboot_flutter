package com.example.springboot_flutter.exception;

/**
 * EmailYaRegistradoException - Excepción cuando se intenta registrar con un email que ya existe
 * HTTP Status: 409 CONFLICT
 */
public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException(String email) {
        super("El email " + email + " ya está registrado");
    }

    public EmailYaRegistradoException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }
}
