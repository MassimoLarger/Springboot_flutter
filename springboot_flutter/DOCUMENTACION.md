# 📝 Documentación - Spring Boot Flutter Project

## Inicio del Proyecto

Se comenzó la creación de una aplicación **Spring Boot** con arquitectura de capas para un backend CRUD y despliegue en Docker.

## Configuración Inicial

El proyecto fue generado desde Spring Initializr (https://start.spring.io) con:

**Tecnologías:**
- Java 21
- Maven
- Spring Boot 4.0.6

**Dependencias Agregadas:**
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- mysql-connector-j
- lombok

## Configuración de Archivos

**pom.xml**: Actualizado con dependencias necesarias para JPA, validación y MySQL

**application.properties**: 
```
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_db
spring.datasource.username=appuser
spring.datasource.password=apppass123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

## Despliegue Docker

Se creó la infraestructura de containerización:

**Dockerfile**: Multi-stage build para compilar y ejecutar la aplicación Spring Boot

**docker-compose.yml**: 
- MySQL 8.0 en puerto 3306
- Spring Boot en puerto 8080
- Base de datos: springboot_db
- Usuario: appuser
- Contraseña: apppass123

**Ejecución:**
```bash
docker-compose up --build
```

## Arquitectura de Capas - FASE 1 Completada

### 1. Capa de Model (Entity)
**Carpeta:** `model/`
**Archivos:**
- `Producto.java` - Entity JPA mapeada a tabla `productos`
  - Campos: id, nombre, descripcion, precio, stock, createdAt, updatedAt, activo
  - Auditoría automática con @PrePersist y @PreUpdate
  - Soft delete con campo activo

### 2. Capa de DTO (Data Transfer Objects)
**Carpeta:** `dto/`
**Archivos:**
- `ProductoRequestDTO.java` - Datos que recibe del cliente
  - Validaciones: @NotBlank, @Size, @DecimalMin, @Min
- `ProductoResponseDTO.java` - Datos que envía al cliente
  - No expone información sensible

### 3. Capa de Mapper
**Carpeta:** `mapper/`
**Archivos:**
- `ProductoMapper.java` - Convierte entre DTOs y Entity
  - Métodos: requestDtoToEntity(), entityToResponseDto(), updateEntityFromDto()

### 4. Capa de Exception Handling
**Carpeta:** `exception/`
**Archivos:**
- `GlobalExceptionHandler.java` - Manejo centralizado de excepciones
- `ProductoNotFoundException.java` - Excepción 404
- `ProductoDuplicadoException.java` - Excepción 409
- `ValidationException.java` - Excepción 400

### 5. Capa de Response
**Carpeta:** `response/`
**Archivos:**
- `ErrorResponse.java` - Formato estándar de errores
- `ApiResponse<T>.java` - Wrapper genérico para respuestas exitosas
- `PaginatedResponse<T>.java` - Wrapper para respuestas paginadas

### 6. Capa de Repository
**Carpeta:** `repository/`
**Proximamente:**
- `ProductoRepository.java` - Interfaz JPA para acceso a datos

### 7. Capa de Service
**Carpeta:** `service/`
**Proximamente:**
- `ProductoService.java` - Interfaz de servicios
- `ProductoServiceImpl.java` - Implementación con lógica de negocio

### 8. Capa de Controller
**Carpeta:** `controller/`
**Proximamente:**
- `ProductoController.java` - Endpoints REST

## Estructura de Respuestas

**Respuesta Exitosa:**
```json
{
  "success": true,
  "data": { /* Producto */ },
  "message": "Operación exitosa",
  "timestamp": "2026-05-18T14:30:00"
}
```

**Respuesta de Error:**
```json
{
  "status": 404,
  "message": "Producto no encontrado",
  "timestamp": "2026-05-18T14:30:00",
  "path": "/api/v1/productos/99"
}
```

## Próximos Pasos

- [ ] FASE 2: Implementar ProductoRepository
- [ ] FASE 3: Implementar ProductoService
- [ ] FASE 4: Implementar ProductoController
- [ ] Tests unitarios e integración
- [ ] Agregar Swagger/OpenAPI

## Estado Actual

**FASE 1 - Fundamentos:** ✅ Completada
- DTOs con validaciones
- Entity con auditoría
- Mapper bidireccional
- Exception handling centralizado
- Respuestas estandarizadas
- Docker configurado

**Archivos creados:** 19 archivos
**Líneas de código:** ~800 líneas

---
*Última actualización: 18 de Mayo, 2026*
