package com.example.springboot_flutter.service;

import com.example.springboot_flutter.dto.AuthResponse;
import com.example.springboot_flutter.dto.LoginRequest;
import com.example.springboot_flutter.dto.RegisterRequest;
import com.example.springboot_flutter.exception.ContrasenasNoCoincidentesException;
import com.example.springboot_flutter.exception.CredencialesInvalidasException;
import com.example.springboot_flutter.exception.EmailYaRegistradoException;
import com.example.springboot_flutter.model.RefreshToken;
import com.example.springboot_flutter.model.Rol;
import com.example.springboot_flutter.model.Usuario;
import com.example.springboot_flutter.repository.RolRepository;
import com.example.springboot_flutter.repository.UsuarioRepository;
import com.example.springboot_flutter.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest request) {
        log.info("Intentando registrar usuario: {}", request.getEmail());

        // Validar email duplicado
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            log.warn("Email ya registrado: {}", request.getEmail());
            throw new EmailYaRegistradoException(request.getEmail());
        }

        // Validar contraseñas
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Contraseñas no coinciden para: {}", request.getEmail());
            throw new ContrasenasNoCoincidentesException();
        }

        // Buscar rol por defecto
        Rol rolUser = rolRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> {
                    log.error("Rol ROLE_USER no encontrado en BD");
                    return new RuntimeException("Rol por defecto no encontrado");
                });

        // Crear usuario
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .activo(true)
                .emailVerificado(false)
                .build();
        
        usuario.addRol(rolUser);

        // Guardar usuario
        usuario = usuarioRepository.save(usuario);
        log.info("Usuario guardado con ID: {}", usuario.getId());

        // Generar token JWT
        String token = jwtProvider.generateTokenFromEmail(usuario.getEmail(), usuario.getId());
        log.info("Token JWT generado para: {}", usuario.getEmail());

        // Generar refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario);
        log.info("Refresh token generado para: {}", usuario.getEmail());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .type("Bearer")
                .usuarioId(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .mensaje("Usuario registrado exitosamente")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para: {}", request.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            usuario.setLastLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);

            String token = jwtProvider.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario);

            log.info("Login exitoso para: {}", request.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken.getToken())
                    .type("Bearer")
                    .usuarioId(usuario.getId())
                    .email(usuario.getEmail())
                    .nombre(usuario.getNombre())
                    .mensaje("Autenticación exitosa")
                    .build();

        } catch (AuthenticationException e) {
            log.error("Login fallido para: {}", request.getEmail(), e);
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }
    }
}