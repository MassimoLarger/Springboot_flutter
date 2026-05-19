package com.example.springboot_flutter.repository;

import com.example.springboot_flutter.model.RefreshToken;
import com.example.springboot_flutter.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUsuario(Usuario usuario);

    void deleteByUsuario(Usuario usuario);

    boolean existsByToken(String token);
}