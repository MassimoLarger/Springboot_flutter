# 🚀 Spring Boot + Flutter Project

Proyecto backend profesional con arquitectura de **4 capas** diseñado para una aplicación Flutter.

## 📋 Estado del Proyecto

### ✅ FASE 1 - FUNDAMENTOS (COMPLETADA)
- [x] DTOs (ProductoRequestDTO, ProductoResponseDTO)
- [x] Entity/Model (Producto)
- [x] Mapper (Conversión DTOs ↔ Entity)
- [x] Global Exception Handler
- [x] Respuestas Estándar (ApiResponse, PaginatedResponse, ErrorResponse)

### ⏳ FASE 2 - Repository Layer (PRÓXIMA)
- [ ] ProductoRepository con JPA
- [ ] Métodos personalizados de búsqueda
- [ ] Soporte para paginación

### ⏳ FASE 3 - Service Layer
- [ ] ProductoService interfaz
- [ ] ProductoServiceImpl con lógica de negocio
- [ ] Transacciones

### ⏳ FASE 4 - Controller Layer
- [ ] ProductoController endpoints REST
- [ ] CRUD completo (GET, POST, PUT, DELETE)

---

## 🛠️ Stack Tecnológico

```
Backend:        Spring Boot 4.0.6 (Java 21)
Database:       MySQL 8.0
Containerization: Docker + Docker Compose
ORM:            JPA / Hibernate
Build Tool:     Maven
```

---

## 🚀 Inicio Rápido

### Con Docker (Recomendado)

```bash
# Clonar/descargar el proyecto
cd springboot_flutter

# Iniciar servicios (MySQL + Spring Boot)
docker-compose up --build

# La API estará disponible en: http://localhost:8080
```

### Sin Docker

```bash
# Asegurar MySQL 8.0 corriendo en localhost:3306

# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# O ejecutar desde IDE
```

---

## 📚 Documentación

- **📖 [Documentación Completa](./docs/DOCUMENTACION.md)** - Lectura completa de toda la arquitectura
- **⚡ [Guía Rápida](./docs/GUIA_RAPIDA.md)** - Referencia rápida para desarrolladores
- **🔧 [Guía de Desarrollo](./docs/GUIA_DESARROLLO.md)** - Cómo agregar nuevas features

---

## 📁 Estructura del Proyecto

```
springboot_flutter/
├── src/
│   └── main/java/com/example/springboot_flutter/
│       ├── dto/                    # Data Transfer Objects
│       ├── model/                  # Entities JPA
│       ├── mapper/                 # Mapeo DTOs ↔ Entity
│       ├── response/               # Response Wrappers
│       ├── exception/              # Exception Handling
│       ├── repository/             # Data Access (FASE 2)
│       ├── service/                # Business Logic (FASE 3)
│       └── controller/             # REST Endpoints (FASE 4)
├── docs/
│   ├── DOCUMENTACION.md            # Documentación completa
│   └── GUIA_RAPIDA.md              # Guía rápida
├── pom.xml                         # Maven configuration
├── Dockerfile                      # Docker image
└── docker-compose.yml              # Docker services
```

---

## 🧪 Probar la API

### Crear un Producto

```bash
curl -X POST http://localhost:8080/api/v1/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Laptop Gaming",
    "descripcion": "RTX 4090, 64GB RAM",
    "precio": 2500.00,
    "stock": 5
  }'
```

### Ver Respuesta

```json
{
  "success": true,
  "data": {
    "id": 1,
    "nombre": "Laptop Gaming",
    "descripcion": "RTX 4090, 64GB RAM",
    "precio": 2500.00,
    "stock": 5,
    "activo": true,
    "createdAt": "2026-05-18T14:30:00",
    "updatedAt": "2026-05-18T14:30:00"
  },
  "message": "Producto creado exitosamente",
  "timestamp": "2026-05-18T14:30:00"
}
```

### Más Ejemplos

Ver **[Guía Rápida](./docs/GUIA_RAPIDA.md)** para más ejemplos de uso.

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────┐
│  CLIENTE (Flutter / Postman / Web)          │
└──────────────────┬──────────────────────────┘
                   │
     ┌─────────────▼─────────────┐
     │  CONTROLLER (FASE 4)      │
     │  Endpoints REST           │
     └─────────────┬─────────────┘
                   │
     ┌─────────────▼──────────────┐
     │  SERVICE (FASE 3)          │
     │  Lógica de Negocio        │
     └─────────────┬──────────────┘
                   │
     ┌─────────────▼──────────────┐
     │  REPOSITORY (FASE 2)       │
     │  Acceso a Datos           │
     └─────────────┬──────────────┘
                   │
     ┌─────────────▼──────────────┐
     │  DATABASE (MySQL)          │
     │  Persistencia              │
     └────────────────────────────┘
```

---

## 🔐 Configuración

### application.properties

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_db
spring.datasource.username=appuser
spring.datasource.password=apppass123

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### Docker Compose

```yaml
- MySQL Port: 3306
- Spring Boot Port: 8080
- DB Name: springboot_db
- DB User: appuser
- DB Password: apppass123
```

---

## 📞 Soporte

- ❓ ¿Preguntas sobre arquitectura? → Ver `docs/DOCUMENTACION.md`
- ⚡ ¿Necesitas referencia rápida? → Ver `docs/GUIA_RAPIDA.md`
- 🔧 ¿Cómo agregar features? → Ver `docs/GUIA_DESARROLLO.md`

---

## 📝 Notas Importantes

1. **Soft Delete**: Los productos se "eliminan" marcándolos como inactivos
2. **Auditoría**: Se registran automáticamente createdAt y updatedAt
3. **Validación**: Las validaciones ocurren en DTOs (entrada) y lógica de negocio
4. **Excepciones**: Centralizadas en GlobalExceptionHandler para respuestas consistentes
5. **DTOs**: Nunca se expone directamente la Entity al cliente

---

## 🚀 Próximos Pasos

1. **FASE 2**: Implementar ProductoRepository con métodos personalizados
2. **FASE 3**: Crear ProductoService con lógica de negocio
3. **FASE 4**: Exponer endpoints en ProductoController
4. **Tests**: Agregar tests unitarios e integración
5. **Documentación API**: Swagger/OpenAPI

---

## 📦 Dependencias Principales

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

---

## 📄 Licencia

Este proyecto es de propósito educativo.

---

**Última actualización**: 18 de Mayo, 2026  
**Versión**: 1.0 (FASE 1 Completa)  
**Desarrollado por**: Senior Developer Assistant
