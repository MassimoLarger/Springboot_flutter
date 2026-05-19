package com.example.springboot_flutter.security;

import com.example.springboot_flutter.exception.ProductoNotFoundException;
import com.example.springboot_flutter.model.Usuario;
import com.example.springboot_flutter.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService - Implementación de UserDetailsService para cargar usuarios
 * 
 * Spring Security usa esta clase para obtener los datos del usuario durante la autenticación
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Carga el usuario por su email (username)
     * 
     * @param email Email del usuario
     * @return UserDetails del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado con email: " + email
                ));

        return CustomUserDetails.from(usuario);
    }

    /**
     * Carga el usuario por su ID
     * 
     * @param usuarioId ID del usuario
     * @return CustomUserDetails del usuario
     * @throws ProductoNotFoundException Si el usuario no existe
     */
    public CustomUserDetails loadUserById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ProductoNotFoundException(
                    "Usuario no encontrado con ID: " + usuarioId
                ));

        return CustomUserDetails.from(usuario);
    }
}
