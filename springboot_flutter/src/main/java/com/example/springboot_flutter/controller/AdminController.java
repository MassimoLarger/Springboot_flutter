package com.example.springboot_flutter.controller;

import com.example.springboot_flutter.model.Usuario;
import com.example.springboot_flutter.repository.UsuarioRepository;
import com.example.springboot_flutter.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints de administración")
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    @Operation(summary = "Listar todos los usuarios", description = "Retorna una lista de todos los usuarios registrados. (Temporalmente sin seguridad para depuración)")
    @GetMapping("/usuarios")
    public ResponseEntity<ApiResponse<List<Usuario>>> listarUsuarios() {
        log.info("Admin solicitando lista de usuarios");
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        // Ocultar contraseñas antes de enviar (aunque sea admin, es buena práctica)
        usuarios.forEach(u -> u.setPassword("********"));
        
        return ResponseEntity.ok(ApiResponse.success(usuarios, "Usuarios obtenidos exitosamente"));
    }
}
