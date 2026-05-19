import 'package:go_router/go_router.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_client/screens/splash_screen.dart';
import 'package:flutter_client/screens/auth/login_screen.dart';
import 'package:flutter_client/screens/auth/register_screen.dart';
import 'package:flutter_client/screens/productos/home_screen.dart';
import 'package:flutter_client/screens/productos/producto_form_screen.dart';
import 'package:flutter_client/screens/productos/producto_detail_screen.dart';
import 'package:flutter_client/data/providers/auth_provider.dart';

class AppRoutes {
  static final router = GoRouter(
    initialLocation: '/splash',
    redirect: (context, state) async {
      final container = ProviderScope.containerOf(context);
      final authState = container.read(authStateProvider);
      
      // Esperar un momento para que se cargue el estado
      await Future.delayed(const Duration(milliseconds: 100));
      
      final currentAuthState = container.read(authStateProvider);
      
      // Si no está autenticado, ir a login
      if (!currentAuthState.isAuthenticated && 
          state.matchedLocation != '/login' && 
          state.matchedLocation != '/register') {
        return '/login';
      }
      
      // Si está autenticado y trata de ir a login/register, ir a home
      if (currentAuthState.isAuthenticated && 
          (state.matchedLocation == '/login' || state.matchedLocation == '/register')) {
        return '/home';
      }
      
      return null;
    },
    routes: [
      GoRoute(
        path: '/splash',
        name: 'splash',
        builder: (context, state) => const SplashScreen(),
      ),
      GoRoute(
        path: '/login',
        name: 'login',
        builder: (context, state) => const LoginScreen(),
      ),
      GoRoute(
        path: '/register',
        name: 'register',
        builder: (context, state) => const RegisterScreen(),
      ),
      GoRoute(
        path: '/home',
        name: 'home',
        builder: (context, state) => const HomeScreen(),
      ),
      GoRoute(
        path: '/producto/nuevo',
        name: 'nuevoProducto',
        builder: (context, state) => const ProductoFormScreen(),
      ),
      GoRoute(
        path: '/producto/editar/:id',
        name: 'editarProducto',
        builder: (context, state) {
          final id = int.parse(state.pathParameters['id']!);
          return ProductoFormScreen(productoId: id);
        },
      ),
      GoRoute(
        path: '/producto/:id',
        name: 'detalleProducto',
        builder: (context, state) {
          final id = int.parse(state.pathParameters['id']!);
          return ProductoDetailScreen(productoId: id);
        },
      ),
    ],
  );
}