package com.example.springboot_flutter.service;

import com.example.springboot_flutter.dto.AuthResponse;
import com.example.springboot_flutter.dto.LoginRequest;
import com.example.springboot_flutter.dto.RegisterRequest;
import com.example.springboot_flutter.exception.ContrasenasNoCoincidentesException;
import com.example.springboot_flutter.exception.EmailYaRegistradoException;
import com.example.springboot_flutter.model.RefreshToken;
import com.example.springboot_flutter.model.Rol;
import com.example.springboot_flutter.model.Usuario;
import com.example.springboot_flutter.repository.RolRepository;
import com.example.springboot_flutter.repository.UsuarioRepository;
import com.example.springboot_flutter.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Usuario usuario;
    private Rol rolUser;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .nombre("Juan Pérez")
                .email("juan@example.com")
                .password("password123")
                .confirmPassword("password123")
                .telefono("+34612345678")
                .build();

        loginRequest = LoginRequest.builder()
                .email("juan@example.com")
                .password("password123")
                .build();

        rolUser = Rol.builder()
                .id(1L)
                .nombre("ROLE_USER")
                .descripcion("Rol de usuario estándar")
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .email("juan@example.com")
                .password("encodedPassword")
                .activo(true)
                .build();

        refreshToken = RefreshToken.builder()
                .token("uuid-refresh-token")
                .usuario(usuario)
                .build();
    }

    @Test
    void register_ShouldCreateUserAndReturnToken() {
        // Arrange
        when(usuarioRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(rolRepository.findByNombre("ROLE_USER")).thenReturn(Optional.of(rolUser));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtProvider.generateTokenFromEmail(usuario.getEmail(), usuario.getId())).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(any(Usuario.class))).thenReturn(refreshToken);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getRefreshToken()).isEqualTo("uuid-refresh-token");
        assertThat(response.getEmail()).isEqualTo("juan@example.com");
        assertThat(response.getNombre()).isEqualTo("Juan Pérez");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldThrowException() {
        // Arrange
        when(usuarioRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailYaRegistradoException.class)
                .hasMessageContaining("juan@example.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void register_WhenPasswordsDoNotMatch_ShouldThrowException() {
        // Arrange
        registerRequest.setConfirmPassword("differentPassword");

        // Act & Assert
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ContrasenasNoCoincidentesException.class);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void login_ShouldAuthenticateAndReturnToken() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(usuarioRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(usuario));
        when(jwtProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(any(Usuario.class))).thenReturn(refreshToken);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getRefreshToken()).isEqualTo("uuid-refresh-token");
        assertThat(response.getEmail()).isEqualTo("juan@example.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }
}
