package com.example.springboot_flutter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "DTO para crear/actualizar un producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDTO {

    @Schema(description = "Nombre del producto", example = "Laptop Gamer", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre del producto es requerido")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "Laptop con procesador Intel i7, 16GB RAM")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @Schema(description = "Precio del producto", example = "1299.99", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.01", inclusive = true, message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999.99", inclusive = true, message = "El precio no puede exceder 999,999.99")
    @Positive(message = "El precio debe ser un valor positivo")
    private BigDecimal precio;

    @Schema(description = "Cantidad disponible en stock", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El stock es requerido")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 999999, message = "El stock no puede exceder 999,999 unidades")
    private Integer stock;
}