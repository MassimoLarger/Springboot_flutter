# Documentación - Spring Boot Project (`springboot_flutter`)

Repositorio: `flutter\springboot_flutter`

## Introducción
Este documento describe la arquitectura, convenciones, configuración y endpoints del backend desarrollado en Spring Boot. El backend implementa:
- API REST con contrato estable (respuestas estándar y paginación).
- DTOs y mapeo para no exponer entidades JPA directamente.
- Manejo global de excepciones con formato de error consistente.
- Autenticación JWT + refresh tokens.
- CORS configurable.
- Documentación OpenAPI/Swagger.
- Infraestructura de despliegue con Docker (multi-stage) + docker-compose (MySQL).
- Pruebas unitarias e integración (según configuración del proyecto).

## Inicio del proyecto
### Generación inicial
Herramienta: Spring Initializr (https://start.spring.io)

Stack base:
- Java 21
- Maven
- Spring Boot 4.0.6

Dependencias base (MVP):
- Spring Web
- MySQL Driver
- Lombok

Evolución del proyecto:
- Se incorporó Spring Data JPA, Validación y Spring Security.
- Se añadió soporte de JWT (jjwt).
- Se estandarizó el contrato de respuesta (éxito/error/paginación).
- Se incorporó documentación OpenAPI/Swagger.
- Se preparó Dockerfile multi-stage y docker-compose con MySQL.

## Arquitectura por capas
Estructura del código fuente:
```text
src/main/java/com/example/springboot_flutter/
├── config/
├── controller/
├── dto/
├── exception/
├── mapper/
├── model/
├── repository/
├── response/
├── security/
└── service/
```

### Capa Model (Entidades)
Carpeta: `model/`

Entidades principales:
- `Producto` (soft delete vía `activo` y timestamps con `@PrePersist/@PreUpdate`)
- `Usuario` (credenciales, estado, roles y timestamps)
- `Rol` (ROLE_USER, ROLE_ADMIN)
- `RefreshToken` (token persistido con expiración, asociado al usuario)

Relaciones de seguridad (alto nivel):
- Usuario ↔ Rol: Many-to-Many.
- Usuario ↔ RefreshToken: One-to-One.

### Capa DTO (Data Transfer Objects)
Carpeta: `dto/`

Archivos:
- `ProductoRequestDTO`: entrada (con validaciones).
- `ProductoResponseDTO`: salida (sin exponer estructura interna).
- `LoginRequest`: credenciales.
- `RegisterRequest`: registro de usuarios.
- `AuthResponse`: token JWT + refresh token.
- `RefreshTokenRequest`: petición para renovar/revocar.
- `TokenRefreshResponse`: respuesta de renovación.

### Capa Mapper
Carpeta: `mapper/`
- `ProductoMapper`: convierte request DTO → entidad, entidad → response DTO y actualiza entidad desde DTO.

### Capa Repository
Carpeta: `repository/`
- `ProductoRepository`: consultas con paginación y filtros.
- `UsuarioRepository`, `RolRepository`, `RefreshTokenRepository`.

### Capa Service
Carpeta: `service/`
- `ProductoService`: lógica de negocio (CRUD + soft delete/restauración).
- `AuthService`: registro/login y emisión de tokens.
- `RefreshTokenService`: emisión y rotación de refresh tokens.

### Capa Controller
Carpeta: `controller/`
- `AuthController`: endpoints de autenticación y renovación de tokens.
- `ProductoController`: endpoints CRUD y búsqueda/paginación.
- `AdminController`: endpoints administrativos (protegidos por rol).

## Hoja de ruta — Backend (Fases 1 a 5)
### Fase 1 — Solidificación del backend
#### 1.1 DTOs + Mapper
Objetivo:
- Recibir datos con `ProductoRequestDTO` y responder con `ProductoResponseDTO`.
- Evitar exponer entidades JPA (`Producto`, `Usuario`, etc.) en la API pública.

Implementación:
- DTOs en `com.example.springboot_flutter.dto`
  - `ProductoRequestDTO`
  - `ProductoResponseDTO`
- Mapper en `com.example.springboot_flutter.mapper`
  - `ProductoMapper` (mapeo manual)

#### 1.2 Manejador global de excepciones
Objetivo:
- Respuestas consistentes para Flutter con estructura estable.

Formato:
```json
{
  "status": 400,
  "message": "Descripción del error",
  "timestamp": "2026-05-17T14:30:00",
  "path": "/ruta"
}
```

Implementación:
- `com.example.springboot_flutter.exception.GlobalExceptionHandler` con `@RestControllerAdvice`.

#### 1.3 Wrapper de respuesta estándar
Objetivo:
- Respuestas uniformes para deserialización simple en Flutter.

Formato:
```json
{
  "success": true,
  "data": { },
  "message": "Operación exitosa",
  "timestamp": "2026-05-17T14:30:00"
}
```

Implementación:
- `com.example.springboot_flutter.response.ApiResponse`
- Respuesta paginada: `com.example.springboot_flutter.response.PaginatedResponse`

#### 1.4 CORS
Objetivo:
- Permitir consumo desde Flutter Web (navegador).

Implementación:
- Configuración en `com.example.springboot_flutter.config.CorsConfig`
- Mapea `/**` y permite métodos: `GET, POST, PUT, DELETE, PATCH, OPTIONS`.
- Orígenes permitidos vía `app.cors.allowed-origins` (propiedades).

### Fase 2 — Seguridad
#### 2.1 Spring Security + JWT
Objetivo:
- Autenticación stateless con JWT.
- Refresh tokens persistidos para renovar sesión.

Componentes principales:
- `SecurityConfig` (reglas y `SecurityFilterChain`)
- `JwtAuthenticationFilter` (valida JWT por request)
- `JwtProvider` (genera/valida JWT)
- `RefreshTokenService` (gestiona refresh tokens)
- `CustomUserDetailsService` + `CustomUserDetails` (integración con Spring Security)

#### 2.2 Protección de endpoints
Reglas principales:
- `/auth/**` público.
- Lectura pública de productos: `GET /productos/**`.
- Administración: `/admin/**` requiere `ROLE_ADMIN`.
- Modificación de productos requiere autenticación.

#### 2.3 Refresh tokens
Estrategia:
- Access token corto (por configuración).
- Refresh token persistido asociado al usuario.
- Rotación de refresh token al renovar access token.

### Fase 3 — Calidad API
#### 3.1 Paginación y filtrado
Objetivo:
- Paginación con `Pageable` en endpoints de listado y búsqueda.

Ejemplos:
- `GET /productos?page=0&size=10&sort=nombre&direction=asc`
- `GET /productos/buscar?nombre=leche&page=0&size=10`

#### 3.2 Validaciones
Objetivo:
- Validar entrada con anotaciones en DTOs y mensajes claros.

#### 3.3 Swagger / OpenAPI
Objetivo:
- Documentación autogenerada para el equipo Flutter.

Rutas:
- UI: `/swagger-ui.html`
- JSON: `/api-docs`

### Fase 4 — Pruebas
Se incluyen pruebas en `src/test/java` (unitarias y contexto de aplicación).
Para tests con H2, existe configuración en `src/test/resources/application.properties`.

Pruebas existentes (referencia del repo):
- `ProductoServiceTest`
- `AuthServiceTest`
- `ProductoControllerTest` (estructura base)
- `ProductoRepositoryTest` (estructura base)
- `SpringbootFlutterApplicationTests`

### Fase 5 — Producción y despliegue
- Perfiles: `application-dev.properties`, `application-prod.properties`.
- Actuator: `/actuator/health`.
- Docker: `Dockerfile` + `docker-compose.yml`.

## Configuración del proyecto
### Propiedades principales
Archivo: `src/main/resources/application.properties`
- `server.port=8080`
- DB MySQL (`spring.datasource.*`)
- JWT:
  - `app.jwt.secret`
  - `app.jwt.expiration`
  - `app.jwt.refresh-expiration`
- CORS:
  - `app.cors.allowed-origins`
- Swagger:
  - `springdoc.api-docs.path=/api-docs`
  - `springdoc.swagger-ui.path=/swagger-ui.html`

### Docker y docker-compose
Archivos:
- `Dockerfile`: build multi-stage (Maven → JRE) + healthcheck.
- `docker-compose.yml`: levanta MySQL 8.0 y el backend en la misma red.

Valores de docker-compose (actual del repo):
- MySQL:
  - `MYSQL_DATABASE=springboot_db`
  - `MYSQL_USER=appuser`
  - `MYSQL_PASSWORD=apppass123`
  - `MYSQL_ROOT_PASSWORD=root123`
  - Puerto: `3306:3306`
  - Volumen: `mysql_data`
  - Healthcheck: `mysqladmin ping`
- Backend:
  - Puerto: `8080:8080`
  - `SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/springboot_db...`
  - `SPRING_DATASOURCE_USERNAME=appuser`
  - `SPRING_DATASOURCE_PASSWORD=apppass123`
  - `SPRING_JPA_HIBERNATE_DDL_AUTO=update`

Comando:
```bash
docker-compose up --build
```

## Contratos de respuesta
### Error (Global)
Siempre retorna:
- `status` (HTTP code)
- `message`
- `timestamp`
- `path`

### Paginación
Respuesta paginada:
```json
{
  "success": true,
  "data": [ { }, { } ],
  "message": "10 de 50 productos encontrados",
  "pagination": {
    "currentPage": 0,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2026-05-17T14:30:00"
}
```

## Endpoints (para Postman)
Base URL: `http://localhost:8080`

### Auth (`/auth`)
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`
- `GET /auth/health`

Headers recomendados:
- `Content-Type: application/json`
- `Accept: application/json`

### Productos (`/productos`)
- `GET /productos`
- `GET /productos/buscar`
- `GET /productos/{id}`
- `POST /productos` (requiere `Authorization: Bearer <token>`)
- `PUT /productos/{id}` (requiere `Authorization: Bearer <token>`)
- `DELETE /productos/{id}` (requiere `Authorization: Bearer <token>`)
- `PATCH /productos/{id}/restaurar` (requiere `ROLE_ADMIN`)

### Admin (`/admin`)
- `GET /admin/usuarios` (requiere `ROLE_ADMIN`)

## Autenticación (Postman)
1) Registrar (opcional):
`POST /auth/register`
2) Login:
`POST /auth/login`
3) Copiar `data.token` y usar en endpoints protegidos:
`Authorization: Bearer <token>`

## Ejecución local y Docker
### Local
- `.\mvnw.cmd spring-boot:run`

### Docker Compose
- `docker-compose up --build`

## Logging y monitoreo
### Logging
- Configuración de logback: `src/main/resources/static/logback-spring.xml`
- Aspecto de logging en perfil dev: `com.example.springboot_flutter.config.LoggingConfig` (logs de requests y servicios).

### Actuator
- Endpoint principal: `GET /actuator/health`
- Métrica personalizada (ejemplo): contador de intentos de login en `com.example.springboot_flutter.config.ActuatorConfig`.
