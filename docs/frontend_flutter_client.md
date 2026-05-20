# Documentación - Flutter Client (`flutter_client`)

Repositorio: `flutter\flutter_client`

## Introducción
Este documento describe lo implementado en el cliente Flutter para consumir la API REST del backend `springboot_flutter`, siguiendo la hoja de ruta de la Fase 6:
- Inicialización del proyecto Flutter (multiplataforma) y dependencias base.
- Capa de datos (modelos, repositorios, cliente HTTP con interceptor JWT + refresh).
- Pantallas CRUD (auth + productos) con validaciones y manejo de estados.

## Fase 6 — Desarrollo del cliente Flutter

### 6.1 Inicialización del proyecto Flutter
Implementación:
- Proyecto Flutter con soporte multiplataforma:
  - Android: `android/`
  - iOS: `ios/`
  - Web: `web/`
  - Windows: `windows/`
  - (También incluye `macos/` y `linux/` por scaffold estándar)

Dependencias instaladas (según [pubspec.yaml](file:///c:/Users/demon/Desktop/Practica/flutter/flutter_client/pubspec.yaml)):
- `dio`: cliente HTTP para consumo de API.
- `flutter_riverpod`: gestión de estado.
- `flutter_secure_storage`: persistencia segura de credenciales/tokens (se usa en lugar de `shared_preferences`).
- `go_router`: navegación y rutas.
- `json_annotation` + `json_serializable`: serialización/deserialización de modelos.

Scripts recomendados:
- Descargar dependencias:
```bash
flutter pub get
```
- Regenerar modelos `.g.dart`:
```bash
dart run build_runner build --delete-conflicting-outputs
```

### 6.2 Capa de datos en Flutter

#### Estructura
```text
lib/
├── data/
│   ├── providers/
│   ├── repositories/
│   └── services/
├── models/
├── routes/
├── screens/
├── utils/
└── widgets/
```

#### Modelos (fromJson/toJson)
Carpeta: `lib/models/`
- [producto.dart](flutter/flutter_client/lib/models/producto.dart) + `producto.g.dart`
- [paginated_response.dart](flutter/flutter_client/lib/models/paginated_response.dart) + `paginated_response.g.dart`
- [auth_response.dart](flutter/flutter_client/lib/models/auth_response.dart) + `auth_response.g.dart`
- `login_request`, `register_request`, `producto_request` (DTOs del cliente)

Todos los modelos usan `json_serializable` con métodos:
- `factory Model.fromJson(Map<String, dynamic> json)`
- `Map<String, dynamic> toJson()`

#### Cliente HTTP (Dio) + interceptor JWT/Refresh
Carpeta: `lib/data/services/`
- [dio_config.dart](flutter/flutter_client/lib/data/services/dio_config.dart):
  - Base URL: `http://localhost:8080`
  - Timeouts: 30s
  - `AuthInterceptor` agrega `Authorization: Bearer <access_token>` a cada request si existe.
  - Manejo de `401`: intenta `POST /auth/refresh` con `refresh_token`, guarda nuevos tokens y reintenta la petición original.
- [api_client.dart](flutter/flutter_client/lib/data/services/api_client.dart):
  - Centraliza llamadas a endpoints (`/auth/*` y `/productos/*`).

#### Repositorios (consumo de API)
Carpeta: `lib/data/repositories/`
- [auth_repository.dart](flutter/flutter_client/lib/data/repositories/auth_repository.dart)
  - Login/registro: persiste tokens y datos básicos de usuario en `flutter_secure_storage`.
  - Logout: revoca refresh token (si existe) y limpia el storage.
- [producto_repository.dart](flutter/flutter_client/lib/data/repositories/producto_repository.dart)
  - Listado y búsqueda paginada con parseo robusto del contrato:
    - `PaginatedResponse` directo (con `pagination` en raíz),
    - o `ApiResponse` que envuelve `data` como objeto paginado,
    - o (fallback) lista plana.

#### Providers (estado con Riverpod)
Carpeta: `lib/data/providers/`
- [auth_provider.dart](flutter/flutter_client/lib/data/providers/auth_provider.dart)
  - `AuthState`: `isLoading`, `error`, `successMessage`, `isAuthenticated`, `user`.
  - Mensajes claros y traducción de errores de Dio (`_getErrorMessage`).
- [producto_provider.dart](flutter/flutter_client/lib/data/providers/producto_provider.dart)
  - `ProductosState`: `productos`, `isLoading`, `error`, `currentPage`, `hasNext`, `isLoadingMore`.
  - Métodos: `loadProductos`, `loadMore`, `buscarProductos`, `createProducto`, `updateProducto`, `deleteProducto`.

### 6.3 Implementación de pantallas CRUD

#### Navegación (GoRouter)
Archivo: [app_routes.dart](flutter/flutter_client/lib/routes/app_routes.dart)
- Rutas:
  - `/splash`
  - `/login`
  - `/register`
  - `/home`
  - `/producto/nuevo`
  - `/producto/editar/:id`
  - `/producto/:id`
- `redirect`: protege rutas cuando no hay sesión (`authState.isAuthenticated`).

Entrada de la app:
- [main.dart](flutter/flutter_client/lib/main.dart) usa `MaterialApp.router` + `ProviderScope`.

#### Auth (Login / Registro)
Pantallas:
- [login_screen.dart](flutter/flutter_client/lib/screens/auth/login_screen.dart)
- [register_screen.dart](flutter/flutter_client/lib/screens/auth/register_screen.dart)

Implementación:
- Conectadas a `authStateProvider` para login/registro.
- Validaciones reutilizables en [validators.dart](flutter/flutter_client/lib/utils/validators.dart).
- Estados:
  - Carga: `isLoading` (muestra indicador).
  - Éxito: SnackBar verde con `successMessage`.
  - Error: SnackBar rojo con mensaje amigable desde backend o Dio.

#### Productos (Listado con paginación)
Pantalla:
- [home_screen.dart](flutter/flutter_client/lib/screens/productos/home_screen.dart)

Implementación:
- Listado con `ListView.builder`.
- Paginación por scroll (`loadMore`) cuando el usuario se acerca al final.
- `RefreshIndicator` para recargar.
- Búsqueda por nombre (campo de búsqueda en AppBar, dispara `buscarProductos`).
- Estado:
  - Loading inicial: `LoadingWidget`.
  - Error sin datos: pantalla de error con botón “Reintentar”.
  - Error con datos: SnackBar rojo (listener a cambios de estado).

#### Productos (Detalle)
Pantalla:
- [producto_detail_screen.dart](flutter/flutter_client/lib/screens/productos/producto_detail_screen.dart)

Implementación:
- Carga y muestra campos principales del producto.
- Acciones:
  - Editar: navega a edición manteniendo stack (`push`).
  - Eliminar: llama a provider, SnackBar de éxito/error y retorna a la pantalla previa (o fallback a `/home`).
- Botón de retroceso explícito con fallback a `/home`.

#### Productos (Crear / Editar)
Pantalla:
- [producto_form_screen.dart](flutter/flutter_client/lib/screens/productos/producto_form_screen.dart)

Implementación:
- Modo crear (sin `productoId`) y modo editar (con `productoId`).
- Validaciones en inputs:
  - nombre requerido y mínimo de caracteres,
  - precio > 0,
  - stock >= 0.
- Estados:
  - Loading durante carga/guardado.
  - Éxito: SnackBar verde (crear/actualizar).
  - Error: SnackBar rojo con mensaje.
- Navegación:
  - Flecha atrás: `pop()` si hay stack, fallback a `/home`.
  - Post-guardar: `pop()` si hay stack, fallback a `/home`.

#### Widgets y utilidades
- Loader reutilizable: [loading_widget.dart](flutter/flutter_client/lib/widgets/loading_widget.dart)
- Tarjeta de producto + navegación al detalle: [producto_card.dart](flutter/flutter_client/lib/widgets/producto_card.dart)

## Verificación del proyecto
Comandos de verificación:
```bash
flutter analyze
flutter test
```

Estado:
- `flutter analyze`: sin issues.
- `flutter test`: OK (test de arranque compatible con Riverpod + GoRouter).