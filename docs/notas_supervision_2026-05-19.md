# Notas de Supervisión — 19-05

## Resumen
Registro de lo visto con el supervisor el día 19-05: herramientas, conceptos y prácticas recomendadas para documentación, despliegue y flujo de trabajo.

## Documentación y diagramas (Draw.io)
### Diagrama de flujos
- Uso: describir procesos paso a paso (ej. login, creación de producto, refresh token, manejo de errores).
- Recomendación: mantener el flujo con decisiones claras (sí/no) y puntos de entrada/salida.

### Mapas mentales
- Uso: organizar ideas, módulos, responsabilidades y dependencias de forma visual.
- Recomendación: separar por dominios (Auth, Productos, Infra, Testing, Deploy).

### Diagramas UML recomendados
- **Casos de uso**: qué actores interactúan con el sistema y con qué funcionalidades.
  - Ej.: Usuario (registrar, login, listar productos, crear producto, editar, eliminar), Admin (restaurar, listar usuarios).
- **Secuencia**: cómo se comunican los componentes por orden temporal.
  - Ej.: Flutter → API `/auth/login` → AuthService → JWT/RefreshToken → respuesta.

## Conceptos (Infra / DevOps)
### SSH (Secure Shell)
- Protocolo seguro para conectarse a servidores remotos (administración, despliegue, diagnóstico).
- Usos típicos: acceso a una VPS/EC2, copiar archivos (SCP/SFTP), ejecutar comandos.

### cPanel
- Panel de administración para hosting (sitios, bases de datos, dominios, correos).
- Útil en hosting tradicional; en cloud moderno se reemplaza frecuentemente por infraestructura gestionada (PaaS/IaC).

### CI/CD (Integración continua / Despliegue continuo)
- **CI**: ejecutar automáticamente compilación, pruebas y validaciones al hacer push o PR.
- **CD**: automatizar despliegues a entornos (staging/prod) una vez que CI pasa.
- Beneficio: menos errores manuales y entregas más consistentes.

### Jenkins
- Herramienta para pipelines (build/test/deploy).
- Ideas de uso: pipeline que construya el backend, ejecute tests y publique artefactos/imagen Docker.

## Herramientas locales
### Laragon (hosting/local stack)
- Entorno local para servir aplicaciones (usualmente PHP/MySQL/Apache/Nginx) y facilitar pruebas rápidas.
- Nota: para este proyecto (Spring Boot) el enfoque principal seguirá siendo Docker/Compose, pero Laragon puede ayudar en pruebas generales de hosting/DB según el contexto.

## Cloud / formación
### AWS / Azure
- Recomendación: practicar y certificarse (training + laboratorios).
- Objetivo: familiarizarse con despliegue, redes, seguridad, servicios gestionados (DB, CI/CD, contenedores).
- Tarea: investigar rutas de aprendizaje y planificar práctica (incluye certificados si aplica).

## Git y estrategia de ramas
### Branching (ramas)
- Crear ramas para trabajar features/bugs sin romper la rama principal.

### Estrategias recomendadas
- **Gitflow**:
  - `main` (producción), `develop` (integración), `feature/*`, `release/*`, `hotfix/*`.
  - Útil cuando hay releases planificados y ciclos definidos.
- **GitHub Flow**:
  - `main` siempre desplegable, ramas cortas `feature/*`, PR + review, merge frecuente.
  - Útil para entrega continua y equipos pequeños/medianos.

### Aclaración: Git vs GitHub
- **Git**: herramienta/sistema de control de versiones (commits, ramas, merges).
- **GitHub**: servicio que aloja repositorios Git y facilita PRs, issues, CI, etc.

## Postman
- Descargar y usar para probar endpoints.
- Buenas prácticas:
  - Colecciones por módulo (`auth`, `productos`, `admin`).
  - Variables de entorno (`baseUrl`, `token`, `refreshToken`).
  - Automatizar pre-request scripts para setear `Authorization: Bearer {{token}}`.

## Acciones sugeridas (próximos pasos)
- Preparar diagramas en Draw.io:
  - Flujo de autenticación (register/login/refresh/logout).
  - CRUD de productos (listado/búsqueda/crear/editar/eliminar/restaurar).
  - Manejo de errores (contrato de `ErrorResponse`).
- Crear un mapa mental del sistema (módulos y dependencias).
- Investigar: AWS training / Azure learning paths y definir plan de práctica.
- Definir estrategia de ramas del equipo (Gitflow o GitHub Flow) y aplicarla en el repositorio.
- Organizar una colección Postman con variables y ejemplos de requests.

