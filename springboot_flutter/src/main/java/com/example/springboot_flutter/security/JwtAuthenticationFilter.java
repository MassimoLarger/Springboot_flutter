package com.example.springboot_flutter.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JwtAuthenticationFilter - Filtro que se ejecuta en CADA petición
 * 
 * Responsabilidades:
 * - Extraer el token JWT del header Authorization
 * - Validar el token
 * - Cargar el usuario y establecer la autenticación en el contexto de Spring Security
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Extraer el token JWT del header Authorization
            String jwt = extractJwtFromRequest(request);

            // Si hay token y es válido
            if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
                // Obtener el email del token
                String email = jwtProvider.getEmailFromToken(jwt);

                // Cargar los detalles del usuario
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Crear token de autenticación
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Establecer detalles de la petición
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Autenticación establecida para usuario: {}", email);
            }
        } catch (Exception ex) {
            log.error("Error al validar el token JWT: {}", ex.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization
     * Formato esperado: Authorization: Bearer <token>
     * 
     * @param request HttpServletRequest
     * @return Token JWT o null si no se encuentra
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remover "Bearer " prefix
        }

        return null;
    }
}
