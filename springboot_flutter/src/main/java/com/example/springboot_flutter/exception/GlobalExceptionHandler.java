package com.example.springboot_flutter.exception;

import com.example.springboot_flutter.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler - Manejador Global de Excepciones
 * 
 * FASE 1.2: Componente centralizado que intercepta todas las excepciones
 * lanzadas en los controllers y las convierte en respuestas HTTP consistentes.
 * 
 * Beneficios:
 * - Respuestas de error uniformes en toda la API
 * - Logging centralizado de errores
 * - Facilita manejo de errores en cliente (Flutter)
 * - Reduce duplicación de código en controllers
 * 
 * Ejemplo de respuesta:
 * HTTP 404
 * {
 *   "status": 404,
 *   "message": "Producto no encontrado con ID: 99",
 *   "timestamp": "2026-05-18T14:30:00",
 *   "path": "/api/v1/productos/99"
 * }
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja ProductoNotFoundException (404 NOT FOUND)
     * Se lanza cuando se intenta acceder a un producto que no existe
     */
    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductoNotFoundException(
            ProductoNotFoundException ex,
            WebRequest request
    ) {
        log.warn("ProductoNotFoundException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Maneja ProductoDuplicadoException (409 CONFLICT)
     * Se lanza cuando se intenta crear un producto con nombre duplicado
     */
    @ExceptionHandler(ProductoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleProductoDuplicadoException(
            ProductoDuplicadoException ex,
            WebRequest request
    ) {
        log.warn("ProductoDuplicadoException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Maneja ValidationException (400 BAD REQUEST)
     * Se lanza cuando los datos no cumplen reglas de validación
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            WebRequest request
    ) {
        log.warn("ValidationException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja MethodArgumentNotValidException (400 BAD REQUEST)
     * Se lanza cuando fallan las validaciones de @Valid en DTOs
     * 
     * Ejemplo: @NotBlank, @DecimalMin, @Min, @Size, etc.
     * Recopila todos los errores de validación en un solo mensaje
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        log.warn("MethodArgumentNotValidException: Errores de validación en el DTO");

        // Recopilar todos los mensajes de error
        String mensajesDeError = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Errores de validación: " + mensajesDeError)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja IllegalArgumentException (400 BAD REQUEST)
     * Excepción genérica para argumentos inválidos
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request
    ) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja EmailYaRegistradoException (409 CONFLICT)
     * Se lanza cuando se intenta registrar con un email que ya existe
     */
    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<ErrorResponse> handleEmailYaRegistradoException(
            EmailYaRegistradoException ex,
            WebRequest request
    ) {
        log.warn("EmailYaRegistradoException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Maneja ContrasenasNoCoincidentesException (400 BAD REQUEST)
     * Se lanza cuando la contraseña no coincide con su confirmación
     */
    @ExceptionHandler(ContrasenasNoCoincidentesException.class)
    public ResponseEntity<ErrorResponse> handleContrasenasNoCoincidentesException(
            ContrasenasNoCoincidentesException ex,
            WebRequest request
    ) {
        log.warn("ContrasenasNoCoincidentesException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja CredencialesInvalidasException (401 UNAUTHORIZED)
     */
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleCredencialesInvalidasException(
            CredencialesInvalidasException ex,
            WebRequest request
    ) {
        log.warn("CredencialesInvalidasException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Maneja cualquier otra excepción no prevista (500 INTERNAL SERVER ERROR)
     * Es el manejador de último recurso para evitar que excepciones inesperadas
     * rompan el flujo normal de la API
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request
    ) {
        log.error("Exception no manejada: ", ex);

        // Durante desarrollo, incluir el mensaje de la excepción para facilitar depuración
        String mensaje = "Error interno del servidor: " + ex.getMessage();
        if (ex.getCause() != null) {
            mensaje += " | Causa: " + ex.getCause().getMessage();
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(mensaje)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
