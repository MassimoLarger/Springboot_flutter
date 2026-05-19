package com.example.springboot_flutter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "DTO de respuesta para producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDTO {

    @Schema(description = "ID único del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Laptop Gamer")
    private String nombre;

    @Schema(description = "Descripción del producto", example = "Laptop con procesador Intel i7, 16GB RAM")
    private String descripcion;

    @Schema(description = "Precio del producto", example = "1299.99")
    private BigDecimal precio;

    @Schema(description = "Cantidad en stock", example = "50")
    private Integer stock;

    @Schema(description = "Indica si el producto está activo", example = "true")
    private Boolean activo;

    @Schema(description = "Fecha de creación", example = "2026-05-18T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización", example = "2026-05-18T14:30:00")
    private LocalDateTime updatedAt;
}