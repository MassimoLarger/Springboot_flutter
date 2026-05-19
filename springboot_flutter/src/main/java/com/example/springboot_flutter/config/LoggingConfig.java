package com.example.springboot_flutter.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Aspect
@Configuration
@Profile("dev")
public class LoggingConfig {

    /**
     * Log de entrada a métodos de servicio
     */
    @Before("execution(* com.example.springboot_flutter.service.*.*(..))")
    public void logBeforeServiceMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        
        log.debug("📤 Servicio: {} - Argumentos: {}", 
                methodName, 
                args.length > 0 ? Arrays.toString(args) : "ninguno");
    }

    /**
     * Log de salida exitosa de métodos de servicio
     */
    @AfterReturning(pointcut = "execution(* com.example.springboot_flutter.service.*.*(..))", 
                    returning = "result")
    public void logAfterServiceMethod(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        
        log.debug("📥 Servicio: {} - Retorno: {}", 
                methodName, 
                result != null ? "éxito" : "void");
    }

    /**
     * Log de excepciones en servicio
     */
    @AfterThrowing(pointcut = "execution(* com.example.springboot_flutter.service.*.*(..))", 
                   throwing = "exception")
    public void logServiceException(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().toShortString();
        
        log.error("❌ Error en servicio: {} - {}", methodName, exception.getMessage());
    }

    /**
     * Log de peticiones HTTP
     */
    @Before("execution(* com.example.springboot_flutter.controller.*.*(..))")
    public void logHttpRequest() {
        ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.info("🌐 [{}] {} - IP: {}", 
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr());
        }
    }

    /**
     * Log de autenticación (auditoría)
     */
    public void logAuthentication(String email, String status) {
        log.info("🔐 AUDIT - Usuario: {} - Status: {}", email, status);
    }
}
