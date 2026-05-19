package com.example.springboot_flutter.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    @Value("${app.jwt.secret:tu_clave_secreta_muy_larga_y_segura_para_jwt_generation_aqui}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:900000}") // 15 minutos en milisegundos
    private long jwtExpirationMs;

    /**
     * Genera un token JWT basado en la autenticación
     * 
     * @param authentication Objeto Authentication de Spring Security
     * @return Token JWT
     */
    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return generateTokenFromEmail(userDetails.getEmail(), userDetails.getId());
    }

    /**
     * Genera un token JWT con email y ID de usuario
     * 
     * @param email Email del usuario
     * @param usuarioId ID del usuario
     * @return Token JWT
     */
    public String generateTokenFromEmail(String email, Long usuarioId) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("usuarioId", usuarioId)
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrae el email (subject) del token JWT
     * 
     * @param token Token JWT
     * @return Email del usuario
     */
    public String getEmailFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Extrae el ID del usuario del token JWT
     * 
     * @param token Token JWT
     * @return ID del usuario
     */
    public Long getUsuarioIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("usuarioId", Long.class);
    }

    /**
     * Valida si un token JWT es válido
     * 
     * @param token Token JWT
     * @return true si es válido, false si no
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida si un token JWT ha expirado
     * 
     * @param token Token JWT
     * @return true si ha expirado, false si sigue siendo válido
     */
    public boolean isTokenExpired(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Obtiene los segundos hasta expiración del token
     * Útil para que Flutter sepa cuándo renovar
     * 
     * @return Segundos hasta expiración
     */
    public long getExpirationSeconds() {
        return jwtExpirationMs / 1000;
    }
}