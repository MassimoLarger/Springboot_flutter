package com.example.springboot_flutter.repository;

import com.example.springboot_flutter.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ProductoRepository - Repositorio para acceder a productos en la BD
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca productos activos paginados
     */
    Page<Producto> findByActivo(Boolean activo, Pageable pageable);

    /**
     * Busca productos por nombre (LIKE) que están activos
     */
    Page<Producto> findByNombreContainingIgnoreCaseAndActivo(String nombre, Boolean activo, Pageable pageable);

    /**
     * Verifica si existe un producto con ese nombre (case-insensitive)
     */
    boolean existsByNombreIgnoreCase(String nombre);
}
