package com.example.springboot_flutter.config;

import com.example.springboot_flutter.model.Rol;
import com.example.springboot_flutter.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer - Inicializa datos base en la BD al arrancar la aplicación
 * 
 * Crea los roles por defecto si no existen
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Inicializando datos base de la aplicación...");

        // Crear rol ROLE_USER si no existe
        if (rolRepository.findByNombre("ROLE_USER").isEmpty()) {
            Rol rolUser = Rol.builder()
                    .nombre("ROLE_USER")
                    .descripcion("Rol de usuario estándar")
                    .build();
            rolRepository.save(rolUser);
            log.info("Rol ROLE_USER creado exitosamente");
        }

        // Crear rol ROLE_ADMIN si no existe
        if (rolRepository.findByNombre("ROLE_ADMIN").isEmpty()) {
            Rol rolAdmin = Rol.builder()
                    .nombre("ROLE_ADMIN")
                    .descripcion("Rol de administrador")
                    .build();
            rolRepository.save(rolAdmin);
            log.info("Rol ROLE_ADMIN creado exitosamente");
        }

        log.info("Inicialización de datos completada");
    }
}
