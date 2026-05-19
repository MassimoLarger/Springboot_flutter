package com.example.springboot_flutter.exception;

/**
 * RefreshTokenException - Excepción para errores relacionados con refresh tokens
 * HTTP Status: 401 UNAUTHORIZED
 */
public class RefreshTokenException extends RuntimeException {

    public RefreshTokenException(String message) {
        super(message);
    }

    public RefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    // Factories para errores comunes
    public static RefreshTokenException tokenNotFound() {
        return new RefreshTokenException("Refresh token no encontrado");
    }

    public static RefreshTokenException tokenExpired() {
        return new RefreshTokenException("Refresh token ha expirado. Por favor, inicie sesión nuevamente");
    }

    public static RefreshTokenException tokenRevoked() {
        return new RefreshTokenException("Refresh token ha sido revocado");
    }

    public static RefreshTokenException invalidToken() {
        return new RefreshTokenException("Refresh token inválido");
    }
}
