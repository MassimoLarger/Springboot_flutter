package com.example.springboot_flutter.mapper;

import com.example.springboot_flutter.dto.ProductoRequestDTO;
import com.example.springboot_flutter.dto.ProductoResponseDTO;
import com.example.springboot_flutter.model.Producto;
import org.springframework.stereotype.Component;

/**
 * ProductoMapper - Convierte entre Entity, Request y Response DTOs
 * 
 * Esta clase implementa el patrón Mapper de forma manual (sin dependencias externas)
 * para mantener la separación entre capas y desacoplar la estructura interna.
 */
@Component
public class ProductoMapper {

    /**
     * Convierte ProductoRequestDTO a Producto Entity
     * Útil cuando el cliente envía datos para crear/actualizar
     */
    public Producto requestDtoToEntity(ProductoRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        return Producto.builder()
                .nombre(requestDTO.getNombre())
                .descripcion(requestDTO.getDescripcion())
                .precio(requestDTO.getPrecio())
                .stock(requestDTO.getStock())
                .activo(true)
                .build();
    }

    /**
     * Convierte Producto Entity a ProductoResponseDTO
     * Útil cuando enviamos datos al cliente
     */
    public ProductoResponseDTO entityToResponseDto(Producto producto) {
        if (producto == null) {
            return null;
        }

        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .activo(producto.getActivo())
                .createdAt(producto.getCreatedAt())
                .updatedAt(producto.getUpdatedAt())
                .build();
    }

    /**
     * Convierte datos de un RequestDTO hacia una Entity existente
     * Útil para actualizar (PATCH/PUT)
     */
    public void updateEntityFromDto(ProductoRequestDTO requestDTO, Producto producto) {
        if (requestDTO == null || producto == null) {
            return;
        }

        if (requestDTO.getNombre() != null) {
            producto.setNombre(requestDTO.getNombre());
        }
        if (requestDTO.getDescripcion() != null) {
            producto.setDescripcion(requestDTO.getDescripcion());
        }
        if (requestDTO.getPrecio() != null) {
            producto.setPrecio(requestDTO.getPrecio());
        }
        if (requestDTO.getStock() != null) {
            producto.setStock(requestDTO.getStock());
        }
    }
}
