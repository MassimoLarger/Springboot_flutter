package com.example.springboot_flutter.exception;

/**
 * ProductoNotFoundException - Excepción cuando no se encuentra un producto
 * 
 * Se lanza cuando se intenta acceder a un producto que no existe en la BD.
 * HTTP Status: 404 NOT FOUND
 */
public class ProductoNotFoundException extends RuntimeException {

    public ProductoNotFoundException(String mensaje) {
        super(mensaje);
    }

    public ProductoNotFoundException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }

    public ProductoNotFoundException(Long id) {
        super("Producto no encontrado con ID: " + id);
    }
}
