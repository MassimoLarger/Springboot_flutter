package com.example.springboot_flutter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Usuario - Representa un usuario en el sistema con autenticación JWT
 * 
 * Implementación de Seguridad
 * - Almacena credenciales (email + password hasheada)
 * - Tiene relación Many-to-Many con Rol
 * - Soporta múltiples roles por usuario (ej: ROLE_ADMIN, ROLE_USER)
 */
@Entity
@Table(name = "usuarios", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 15)
    private String telefono;

    /**
     * Indica si la cuenta está activa
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    /**
     * Indica si el email ha sido verificado
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean emailVerificado = false;

    /**
     * Relación Many-to-Many con Rol
     * Un usuario puede tener múltiples roles
     * Fetch.EAGER para cargar roles inmediatamente al obtener usuario
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_rol",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    /**
     * Fecha de creación
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha de última actualización
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Última fecha de login
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * Hook JPA que se ejecuta antes de insertar
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Hook JPA que se ejecuta antes de actualizar
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Método auxiliar para agregar un rol
     */
    public void addRol(Rol rol) {
        this.roles.add(rol);
    }

    /**
     * Método auxiliar para remover un rol
     */
    public void removeRol(Rol rol) {
        this.roles.remove(rol);
    }
}