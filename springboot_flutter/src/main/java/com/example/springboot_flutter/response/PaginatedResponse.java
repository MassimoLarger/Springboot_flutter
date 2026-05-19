package com.example.springboot_flutter.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PaginatedResponse<T> - Wrapper para Respuestas Paginadas
 * 
 * FASE 1.3: Clase genérica que envuelve respuestas con paginación.
 * Extiende ApiResponse para incluir información de paginación útil para Flutter.
 * 
 * Beneficios:
 * - Flutter sabe cuántas páginas hay en total
 * - Facilita la implementación de "lazy loading" (cargar más items al scroll)
 * - Información de ordenamiento y filtros
 * 
 * Ejemplo de respuesta paginada:
 * HTTP 200
 * {
 *   "success": true,
 *   "data": [
 *     { "id": 1, "nombre": "Laptop", ... },
 *     { "id": 2, "nombre": "Mouse", ... }
 *   ],
 *   "message": "10 de 50 productos encontrados",
 *   "timestamp": "2026-05-18T14:30:00",
 *   "pagination": {
 *     "currentPage": 0,
 *     "pageSize": 10,
 *     "totalElements": 50,
 *     "totalPages": 5,
 *     "isFirst": true,
 *     "isLast": false,
 *     "hasNext": true,
 *     "hasPrevious": false
 *   }
 * }
 *
 * @param <T> Tipo de dato genérico que irá en el campo data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> {

    /**
     * Indica si la operación fue exitosa
     */
    private Boolean success;

    /**
     * Lista de datos de la página actual
     */
    private List<T> data;

    /**
     * Mensaje descriptivo (ej: "10 de 50 productos encontrados")
     */
    private String message;

    /**
     * Información de paginación
     */
    private PaginationInfo pagination;

    /**
     * Fecha y hora de la respuesta
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Método estático para crear respuestas paginadas desde Page<T>
     * Uso: PaginatedResponse.success(page, "10 de 50 productos")
     */
    public static <T> PaginatedResponse<T> success(Page<T> page, String message) {
        return PaginatedResponse.<T>builder()
                .success(true)
                .data(page.getContent())
                .message(message)
                .pagination(PaginationInfo.fromPage(page))
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Clase interna para información de paginación
     * Contiene todos los datos que Flutter necesita para navegar
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationInfo {

        /**
         * Número de página actual (0-indexed)
         */
        private Integer currentPage;

        /**
         * Cantidad de elementos en la página actual
         */
        private Integer pageSize;

        /**
         * Total de elementos en todas las páginas
         */
        private Long totalElements;

        /**
         * Total de páginas disponibles
         */
        private Integer totalPages;

        /**
         * ¿Es esta la primera página?
         */
        private Boolean isFirst;

        /**
         * ¿Es esta la última página?
         */
        private Boolean isLast;

        /**
         * ¿Hay página siguiente?
         */
        private Boolean hasNext;

        /**
         * ¿Hay página anterior?
         */
        private Boolean hasPrevious;

        /**
         * Crea un PaginationInfo desde un Page<T>
         */
        public static PaginationInfo fromPage(Page<?> page) {
            return PaginationInfo.builder()
                    .currentPage(page.getNumber())
                    .pageSize(page.getSize())
                    .totalElements(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .isFirst(page.isFirst())
                    .isLast(page.isLast())
                    .hasNext(page.hasNext())
                    .hasPrevious(page.hasPrevious())
                    .build();
        }
    }
}
