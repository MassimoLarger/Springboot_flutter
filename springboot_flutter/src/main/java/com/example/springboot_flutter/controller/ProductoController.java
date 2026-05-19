package com.example.springboot_flutter.controller;

import com.example.springboot_flutter.dto.ProductoRequestDTO;
import com.example.springboot_flutter.dto.ProductoResponseDTO;
import com.example.springboot_flutter.response.ApiResponse;
import com.example.springboot_flutter.response.PaginatedResponse;
import com.example.springboot_flutter.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints para gestión de productos")
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Obtiene todos los productos con paginación y ordenamiento
     */
    @Operation(
            summary = "Obtener todos los productos",
            description = "Retorna una lista paginada de productos activos. Soporta ordenamiento por nombre, precio, stock o fecha de creación."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<ProductoResponseDTO>> obtenerTodos(
            @Parameter(description = "Número de página (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Cantidad de elementos por página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenar (nombre, precio, stock, createdAt)", example = "nombre")
            @RequestParam(defaultValue = "nombre") String sort,
            
            @Parameter(description = "Dirección de ordenamiento (asc o desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Solicitud de productos - página: {}, tamaño: {}, orden: {} {}", page, size, sort, direction);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        String sortField = validateSortField(sort);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        Page<ProductoResponseDTO> productosPage = productoService.obtenerTodos(pageable);
        
        String message = String.format("%d de %d productos encontrados", 
                productosPage.getNumberOfElements(), 
                productosPage.getTotalElements());
        
        return ResponseEntity.ok(PaginatedResponse.success(productosPage, message));
    }

    /**
     * Busca productos por nombre (con paginación)
     */
    @Operation(
            summary = "Buscar productos por nombre",
            description = "Busca productos cuyo nombre contenga el texto especificado (case-insensitive). Soporta paginación."
    )
    @GetMapping("/buscar")
    public ResponseEntity<PaginatedResponse<ProductoResponseDTO>> buscarPorNombre(
            @Parameter(description = "Término de búsqueda", required = true, example = "laptop")
            @RequestParam String nombre,
            
            @Parameter(description = "Número de página (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Cantidad de elementos por página", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Búsqueda de productos - término: {}, página: {}, tamaño: {}", nombre, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponseDTO> productosPage = productoService.buscarPorNombre(nombre, pageable);
        
        String message = String.format("%d productos encontrados para '%s'", 
                productosPage.getTotalElements(), 
                nombre);
        
        return ResponseEntity.ok(PaginatedResponse.success(productosPage, message));
    }

    /**
     * Obtiene un producto por su ID
     */
    @Operation(
            summary = "Obtener producto por ID",
            description = "Retorna un producto específico basado en su ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> obtenerPorId(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.info("Solicitud de producto con ID: {}", id);
        
        ProductoResponseDTO producto = productoService.obtenerPorId(id);
        
        return ResponseEntity.ok(ApiResponse.success(producto, "Producto obtenido exitosamente"));
    }

    /**
     * Crea un nuevo producto
     */
    @Operation(
            summary = "Crear nuevo producto",
            description = "Crea un producto con los datos proporcionados. Requiere autenticación.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Producto duplicado")
    })
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> crear(
            @Valid @RequestBody ProductoRequestDTO requestDTO
    ) {
        log.info("Solicitud de creación de producto: {}", requestDTO.getNombre());
        
        ProductoResponseDTO nuevoProducto = productoService.crear(requestDTO);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(nuevoProducto, "Producto creado exitosamente"));
    }

    /**
     * Actualiza un producto existente
     */
    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza un producto existente. Requiere autenticación.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> actualizar(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long id,
            
            @Valid @RequestBody ProductoRequestDTO requestDTO
    ) {
        log.info("Solicitud de actualización de producto con ID: {}", id);
        
        ProductoResponseDTO productoActualizado = productoService.actualizar(id, requestDTO);
        
        return ResponseEntity.ok(ApiResponse.success(productoActualizado, "Producto actualizado exitosamente"));
    }

    /**
     * Elimina (desactiva) un producto
     */
    @Operation(
            summary = "Eliminar producto",
            description = "Elimina (soft delete) un producto. Requiere autenticación.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.info("Solicitud de eliminación de producto con ID: {}", id);
        
        productoService.eliminar(id);
        
        return ResponseEntity.ok(ApiResponse.success("Producto eliminado exitosamente"));
    }

    /**
     * Restaura un producto previamente eliminado
     * Requiere rol ADMIN
     */
    @Operation(
            summary = "Restaurar producto",
            description = "Restaura un producto previamente eliminado. Requiere rol ADMIN.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto restaurado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No autorizado - se requiere ADMIN"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/{id}/restaurar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> restaurar(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.info("Solicitud de restauración de producto con ID: {}", id);
        
        productoService.restaurar(id);
        
        return ResponseEntity.ok(ApiResponse.success("Producto restaurado exitosamente"));
    }

    /**
     * Valida que el campo de ordenamiento sea permitido
     */
    private String validateSortField(String sort) {
        String[] allowedFields = {"nombre", "precio", "stock", "createdAt"};
        
        for (String allowed : allowedFields) {
            if (allowed.equalsIgnoreCase(sort)) {
                return allowed;
            }
        }
        
        log.warn("Campo de ordenamiento no permitido: {}, usando default 'nombre'", sort);
        return "nombre";
    }
}