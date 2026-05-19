package com.example.springboot_flutter.security;

import com.example.springboot_flutter.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * CustomUserDetails - Implementación personalizada de UserDetails para Spring Security
 * Adapta la entidad Usuario a los requisitos de Spring Security
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private Boolean activo;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Crea una instancia de CustomUserDetails a partir de una entidad Usuario
     * 
     * @param usuario Entidad Usuario de la BD
     * @return CustomUserDetails listo para usar en Spring Security
     */
    public static CustomUserDetails from(Usuario usuario) {
        Collection<? extends GrantedAuthority> authorities = usuario.getRoles()
                .stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toList());

        return CustomUserDetails.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .activo(usuario.getActivo())
                .authorities(authorities)
                .build();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
