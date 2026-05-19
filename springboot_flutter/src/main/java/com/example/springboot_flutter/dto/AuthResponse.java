package com.example.springboot_flutter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String refreshToken;

    @Builder.Default
    private String type = "Bearer";

    private Long usuarioId;
    private String email;
    private String nombre;
    private String mensaje;
}