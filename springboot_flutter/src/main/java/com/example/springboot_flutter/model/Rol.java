package com.example.springboot_flutter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Rol - Define los roles de acceso en el sistema
 * Ejemplos: ROLE_ADMIN, ROLE_USER
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String nombre;

    /**
     * Descripción del rol
     */
    @Column(length = 255)
    private String descripcion;
}
