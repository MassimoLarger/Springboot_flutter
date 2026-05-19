package com.example.springboot_flutter.exception;

/**
 * ValidationException - Excepción para errores de validación
 * 
 * Se lanza cuando los datos proporcionados no cumplen las reglas de validación.
 * HTTP Status: 400 BAD REQUEST
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String mensaje) {
        super(mensaje);
    }

    public ValidationException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }
}
