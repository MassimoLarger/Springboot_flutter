package com.example.springboot_flutter.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ApiResponse<T> - Wrapper Genérico de Respuesta
 * 
 * FASE 1.3: Clase genérica que envuelve todas las respuestas exitosas de la API.
 * Proporciona un contrato claro y predecible para que Flutter pueda deserializar
 * las respuestas de forma consistente.
 * 
 * Beneficios:
 * - Formato unificado en toda la API
 * - Fácil deserialización en Flutter
 * - Incluye metadata útil (éxito, mensaje, timestamp)
 * - Soporte para cualquier tipo de dato genérico
 * 
 * Ejemplo de respuesta simple (objeto único):
 * HTTP 200
 * {
 *   "success": true,
 *   "data": {
 *     "id": 1,
 *     "nombre": "Laptop",
 *     "precio": 1500.00,
 *     "stock": 10,
 *     "activo": true,
 *     "createdAt": "2026-05-18T14:30:00",
 *     "updatedAt": "2026-05-18T14:30:00"
 *   },
 *   "message": "Producto obtenido exitosamente",
 *   "timestamp": "2026-05-18T14:30:00"
 * }
 * 
 * Ejemplo de respuesta con lista:
 * HTTP 200
 * {
 *   "success": true,
 *   "data": [
 *     { "id": 1, "nombre": "Laptop", ... },
 *     { "id": 2, "nombre": "Mouse", ... }
 *   ],
 *   "message": "5 productos encontrados",
 *   "timestamp": "2026-05-18T14:30:00"
 * }
 *
 * @param <T> Tipo de dato genérico que irá en el campo data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    /**
     * Indica si la operación fue exitosa (true) o falló (false)
     * En controladores normalmente será true ya que los errores
     * se manejan en GlobalExceptionHandler
     */
    private Boolean success;

    /**
     * Datos de la respuesta. Puede ser:
     * - Un objeto único (ProductoResponseDTO)
     * - Una lista (List<ProductoResponseDTO>)
     * - Una página (Page<ProductoResponseDTO>)
     * - null (para operaciones sin retorno)
     */
    private T data;

    /**
     * Mensaje descriptivo de la operación
     * Ejemplos:
     * - "Producto creado exitosamente"
     * - "3 productos encontrados"
     * - "Operación completada sin problemas"
     */
    private String message;

    /**
     * Fecha y hora de la respuesta
     * Útil para auditoría y debugging
     * Formato: ISO 8601 (yyyy-MM-ddTHH:mm:ss)
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Constructor de conveniencia para respuestas exitosas
     * Uso: new ApiResponse<>(true, producto, "Producto obtenido")
     */
    public ApiResponse(Boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Método estático para crear respuestas exitosas más fácilmente
     * Uso: ApiResponse.success(producto, "Producto creado")
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Método estático para crear respuestas sin datos
     * Uso: ApiResponse.success("Producto eliminado")
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(null)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Método estático para crear respuestas de error
     * Nota: Normalmente no se usa aquí, sino en GlobalExceptionHandler
     */
    public static <T> ApiResponse<T> error(T data, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
