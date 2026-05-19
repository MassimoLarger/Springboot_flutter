package com.example.springboot_flutter.controller;

import com.example.springboot_flutter.dto.AuthResponse;
import com.example.springboot_flutter.dto.LoginRequest;
import com.example.springboot_flutter.dto.RefreshTokenRequest;
import com.example.springboot_flutter.dto.RegisterRequest;
import com.example.springboot_flutter.dto.TokenRefreshResponse;
import com.example.springboot_flutter.response.ApiResponse;
import com.example.springboot_flutter.service.AuthService;
import com.example.springboot_flutter.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    /**
     * Endpoint para registro de nuevos usuarios
     * 
     * @param request Datos del registro
     * @return Token JWT y Refresh Token para el nuevo usuario
     * 
     * Ejemplo de request:
     * {
     *   "nombre": "Juan Pérez",
     *   "email": "juan@example.com",
     *   "password": "password123",
     *   "confirmPassword": "password123",
     *   "telefono": "+34612345678"
     * }
     * 
     * Ejemplo de response:
     * HTTP 201
     * {
     *   "success": true,
     *   "data": {
     *     "token": "eyJhbGciOiJIUzUxMiJ9...",
     *     "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
     *     "type": "Bearer",
     *     "usuarioId": 1,
     *     "email": "juan@example.com",
     *     "nombre": "Juan Pérez",
     *     "mensaje": "Usuario registrado exitosamente"
     *   },
     *   "message": "Registro exitoso",
     *   "timestamp": "2026-05-18T14:30:00"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("Solicitud de registro para: {}", request.getEmail());

        AuthResponse authResponse = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(authResponse, "Registro exitoso"));
    }

    /**
     * Endpoint para autenticación (login)
     * 
     * @param request Credenciales (email, password)
     * @return Token JWT y Refresh Token del usuario autenticado
     * 
     * Ejemplo de request:
     * {
     *   "email": "juan@example.com",
     *   "password": "password123"
     * }
     * 
     * Ejemplo de response:
     * HTTP 200
     * {
     *   "success": true,
     *   "data": {
     *     "token": "eyJhbGciOiJIUzUxMiJ9...",
     *     "refreshToken": "550e8400-e29b-41d4-a716-446655440001",
     *     "type": "Bearer",
     *     "usuarioId": 1,
     *     "email": "juan@example.com",
     *     "nombre": "Juan Pérez",
     *     "mensaje": "Autenticación exitosa"
     *   },
     *   "message": "Login exitoso",
     *   "timestamp": "2026-05-18T14:30:00"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("Solicitud de login para: {}", request.getEmail());

        AuthResponse authResponse = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(authResponse, "Login exitoso"));
    }

    /**
     * Endpoint para renovar access token usando refresh token
     * 
     * @param request Refresh token
     * @return Nuevo par de tokens (access + refresh)
     * 
     * Ejemplo de request:
     * {
     *   "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
     * }
     * 
     * Ejemplo de response:
     * HTTP 200
     * {
     *   "success": true,
     *   "data": {
     *     "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
     *     "refreshToken": "550e8400-e29b-41d4-a716-446655440002",
     *     "type": "Bearer",
     *     "expiresIn": 900,
     *     "message": "Token renovado exitosamente"
     *   },
     *   "message": "Token renovado exitosamente",
     *   "timestamp": "2026-05-18T14:30:00"
     * }
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        log.info("Solicitud de renovación de token");
        
        TokenRefreshResponse response = refreshTokenService.refreshAccessToken(request.getRefreshToken());
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Token renovado exitosamente"));
    }

    /**
     * Endpoint para logout - Revoca el refresh token
     * 
     * @param request Refresh token a revocar
     * 
     * Ejemplo de request:
     * {
     *   "refreshToken": "550e8400-e29b-41d4-a716-446655440001"
     * }
     * 
     * Ejemplo de response:
     * HTTP 200
     * {
     *   "success": true,
     *   "data": null,
     *   "message": "Logout exitoso",
     *   "timestamp": "2026-05-18T14:30:00"
     * }
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        log.info("Solicitud de logout");
        
        refreshTokenService.deleteRefreshToken(request.getRefreshToken());
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Logout exitoso"));
    }

    /**
     * Endpoint de prueba para verificar que el servidor está activo
     * (No requiere autenticación)
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("OK", "Servidor disponible"));
    }
}