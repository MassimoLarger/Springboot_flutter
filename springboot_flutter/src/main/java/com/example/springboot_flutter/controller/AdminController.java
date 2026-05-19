package com.example.springboot_flutter.controller;

import com.example.springboot_flutter.model.Usuario;
import com.example.springboot_flutter.repository.UsuarioRepository;
import com.example.springboot_flutter.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/usuarios")
    public ApiResponse<List<Usuario>> getUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ApiResponse.success(usuarios, "Usuarios encontrados: " + usuarios.size());
    }

    @GetMapping("/usuarios/{id}")
    public ApiResponse<Usuario> getUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ApiResponse.success(usuario, "Usuario encontrado");
    }
}