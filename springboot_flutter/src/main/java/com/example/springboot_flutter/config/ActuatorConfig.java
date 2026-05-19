package com.example.springboot_flutter.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ActuatorConfig - Configuración de Métricas e Info
 * 
 * FASE 5: Monitoreo y métricas para producción
 * Nota: Health checks y Info contributors se configuran via application.properties
 */
@Configuration
public class ActuatorConfig {

    @Value("${spring.application.name:springboot-flutter-api}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    /**
     * Métrica personalizada para contar intentos de login
     */
    @Bean
    public Counter loginAttemptsCounter(MeterRegistry registry) {
        return Counter.builder("auth.login.attempts")
                .tag("type", "total")
                .description("Total de intentos de login")
                .register(registry);
    }
}