package com.example.springboot_flutter.repository;

import com.example.springboot_flutter.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RolRepository - Repositorio para acceder a los roles en la BD
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Busca un rol por su nombre
     * 
     * @param nombre Nombre del rol
     * @return Optional con el rol si existe
     */
    Optional<Rol> findByNombre(String nombre);
}
