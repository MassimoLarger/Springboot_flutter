package com.example.springboot_flutter.service;

import com.example.springboot_flutter.dto.ProductoRequestDTO;
import com.example.springboot_flutter.dto.ProductoResponseDTO;
import com.example.springboot_flutter.exception.ProductoDuplicadoException;
import com.example.springboot_flutter.exception.ProductoNotFoundException;
import com.example.springboot_flutter.mapper.ProductoMapper;
import com.example.springboot_flutter.model.Producto;
import com.example.springboot_flutter.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductoService - Servicio para la lógica de negocio de productos
 * 
 * FASE 2.2: Métodos de lectura son públicos, modificación requiere autenticación/autorización
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    /**
     * Obtiene todos los productos (paginados)
     * 
     * @param pageable Información de paginación
     * @return Página de productos
     */
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> obtenerTodos(Pageable pageable) {
        log.info("Obteniendo productos con paginación: {}", pageable);
        return productoRepository.findByActivo(true, pageable)
                .map(productoMapper::entityToResponseDto);
    }

    /**
     * Obtiene un producto por ID
     * 
     * @param id ID del producto
     * @return ProductoResponseDTO
     * @throws ProductoNotFoundException Si el producto no existe
     */
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        log.info("Obteniendo producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con ID: " + id));
        
        return productoMapper.entityToResponseDto(producto);
    }

    /**
     * Busca productos por nombre (LIKE)
     * 
     * @param nombre Nombre del producto a buscar
     * @param pageable Información de paginación
     * @return Página de productos encontrados
     */
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> buscarPorNombre(String nombre, Pageable pageable) {
        log.info("Buscando productos con nombre: {}", nombre);
        return productoRepository.findByNombreContainingIgnoreCaseAndActivo(nombre, true, pageable)
                .map(productoMapper::entityToResponseDto);
    }

    /**
     * Crea un nuevo producto
     * REQUIERE: Autenticación de usuario
     * 
     * @param requestDTO Datos del producto a crear
     * @return ProductoResponseDTO del producto creado
     * @throws ProductoDuplicadoException Si ya existe un producto con ese nombre
     */
    public ProductoResponseDTO crear(ProductoRequestDTO requestDTO) {
        log.info("Creando nuevo producto: {}", requestDTO.getNombre());

        // Verificar si el nombre ya existe
        if (productoRepository.existsByNombreIgnoreCase(requestDTO.getNombre())) {
            throw new ProductoDuplicadoException(requestDTO.getNombre());
        }

        // Mapear DTO a Entity
        Producto producto = productoMapper.requestDtoToEntity(requestDTO);

        // Guardar
        producto = productoRepository.save(producto);

        log.info("Producto creado exitosamente con ID: {}", producto.getId());
        return productoMapper.entityToResponseDto(producto);
    }

    /**
     * Actualiza un producto existente
     * REQUIERE: Autenticación de usuario
     * 
     * @param id ID del producto
     * @param requestDTO Nuevos datos del producto
     * @return ProductoResponseDTO del producto actualizado
     * @throws ProductoNotFoundException Si el producto no existe
     * @throws ProductoDuplicadoException Si el nuevo nombre ya existe en otro producto
     */
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO requestDTO) {
        log.info("Actualizando producto con ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con ID: " + id));

        // Verificar si el nuevo nombre ya existe en otro producto
        if (!producto.getNombre().equalsIgnoreCase(requestDTO.getNombre()) &&
            productoRepository.existsByNombreIgnoreCase(requestDTO.getNombre())) {
            throw new ProductoDuplicadoException(requestDTO.getNombre());
        }

        // Actualizar campos
        productoMapper.updateEntityFromDto(requestDTO, producto);

        // Guardar cambios
        producto = productoRepository.save(producto);

        log.info("Producto actualizado exitosamente");
        return productoMapper.entityToResponseDto(producto);
    }

    /**
     * Elimina (desactiva) un producto
     * REQUIERE: Autenticación de usuario
     * 
     * @param id ID del producto a eliminar
     * @throws ProductoNotFoundException Si el producto no existe
     */
    public void eliminar(Long id) {
        log.info("Eliminando producto con ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con ID: " + id));

        // Marcar como inactivo (soft delete)
        producto.setActivo(false);
        productoRepository.save(producto);

        log.info("Producto eliminado exitosamente");
    }

    /**
     * Restaura un producto previamente eliminado
     * REQUIERE: Rol ADMIN
     * 
     * @param id ID del producto a restaurar
     * @throws ProductoNotFoundException Si el producto no existe
     */
    public void restaurar(Long id) {
        log.info("Restaurando producto con ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado con ID: " + id));

        // Marcar como activo
        producto.setActivo(true);
        productoRepository.save(producto);

        log.info("Producto restaurado exitosamente");
    }
}
