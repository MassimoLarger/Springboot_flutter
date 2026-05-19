package com.example.springboot_flutter.exception;

/**
 * ProductoDuplicadoException - Excepción cuando se intenta crear un producto duplicado
 * 
 * Se lanza cuando se intenta crear un producto con un nombre que ya existe.
 * HTTP Status: 409 CONFLICT
 */
public class ProductoDuplicadoException extends RuntimeException {

    public ProductoDuplicadoException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }

    public ProductoDuplicadoException(String nombre) {
        super("Ya existe un producto con el nombre: " + nombre);
    }
}
