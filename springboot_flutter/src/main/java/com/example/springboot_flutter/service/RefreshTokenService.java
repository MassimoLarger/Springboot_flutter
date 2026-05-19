package com.example.springboot_flutter.service;

import com.example.springboot_flutter.dto.TokenRefreshResponse;
import com.example.springboot_flutter.exception.RefreshTokenException;
import com.example.springboot_flutter.model.RefreshToken;
import com.example.springboot_flutter.model.Usuario;
import com.example.springboot_flutter.repository.RefreshTokenRepository;
import com.example.springboot_flutter.repository.UsuarioRepository;
import com.example.springboot_flutter.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtProvider jwtProvider;

    @Value("${app.jwt.refresh-expiration:604800000}") // 7 días en milisegundos (default)
    private long refreshTokenDurationMs;

    /**
     * Crea un nuevo refresh token para un usuario
     * 
     * @param usuario Usuario al que pertenece el token
     * @return RefreshToken creado
     */
    public RefreshToken createRefreshToken(Usuario usuario) {
        // Eliminar refresh token anterior si existe
        refreshTokenRepository.findByUsuario(usuario)
                .ifPresent(refreshTokenRepository::delete);

        // Crear nuevo refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(usuario)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token creado para usuario: {}", usuario.getEmail());

        return refreshToken;
    }

    /**
     * Verifica y renueva un access token usando un refresh token
     * 
     * @param refreshTokenString Refresh token a validar
     * @return TokenRefreshResponse con nuevos tokens
     * @throws RefreshTokenException Si el token es inválido, expirado o revocado
     */
    public TokenRefreshResponse refreshAccessToken(String refreshTokenString) {
        log.info("Solicitando renovación de access token");

        // Buscar refresh token en BD
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(RefreshTokenException::tokenNotFound);

        // Verificar si está revocado
        if (refreshToken.getRevoked()) {
            log.warn("Refresh token revocado para usuario: {}", refreshToken.getUsuario().getEmail());
            throw RefreshTokenException.tokenRevoked();
        }

        // Verificar si ha expirado
        if (refreshToken.isExpired()) {
            log.warn("Refresh token expirado para usuario: {}", refreshToken.getUsuario().getEmail());
            // Eliminar token expirado
            refreshTokenRepository.delete(refreshToken);
            throw RefreshTokenException.tokenExpired();
        }

        // Obtener usuario
        Usuario usuario = refreshToken.getUsuario();

        // Verificar que el usuario esté activo
        if (!usuario.getActivo()) {
            log.warn("Usuario inactivo intentando refrescar token: {}", usuario.getEmail());
            throw RefreshTokenException.invalidToken();
        }

        // Generar nuevos tokens
        String newAccessToken = jwtProvider.generateTokenFromEmail(usuario.getEmail(), usuario.getId());
        
        // Opcional: Rotación de refresh token (crear nuevo y revocar el anterior)
        RefreshToken newRefreshToken = createRefreshToken(usuario);
        
        // Revocar el refresh token usado
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Access token renovado exitosamente para usuario: {}", usuario.getEmail());

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .type("Bearer")
                .expiresIn(jwtProvider.getExpirationSeconds())
                .message("Token renovado exitosamente")
                .build();
    }

    /**
     * Revoca todos los refresh tokens de un usuario
     * Útil para logout o cambio de contraseña
     * 
     * @param usuarioId ID del usuario
     */
    public void revokeAllUserTokens(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        refreshTokenRepository.findByUsuario(usuario)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    log.info("Refresh tokens revocados para usuario: {}", usuario.getEmail());
                });
    }

    /**
     * Elimina un refresh token específico
     * 
     * @param refreshTokenString Token a eliminar
     */
    public void deleteRefreshToken(String refreshTokenString) {
        refreshTokenRepository.findByToken(refreshTokenString)
                .ifPresent(refreshTokenRepository::delete);
    }
}