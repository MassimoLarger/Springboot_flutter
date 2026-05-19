package com.example.springboot_flutter.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ErrorResponse - Respuesta estandarizada de errores
 * 
 * Esta clase define el formato consistente de todas las respuestas de error
 * que la API devuelve a los clientes (Flutter, Postman, etc).
 * 
 * Ejemplo de respuesta:
 * {
 *   "status": 400,
 *   "message": "El nombre del producto es requerido",
 *   "timestamp": "2026-05-18T14:30:00"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * Código HTTP del error (400, 404, 500, etc)
     */
    private Integer status;

    /**
     * Mensaje descriptivo del error
     */
    private String message;

    /**
     * Fecha y hora en que ocurrió el error
     * Formato: ISO 8601 (yyyy-MM-ddTHH:mm:ss)
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Ruta del endpoint donde ocurrió el error (opcional)
     * Útil para debugging
     */
    private String path;
}
