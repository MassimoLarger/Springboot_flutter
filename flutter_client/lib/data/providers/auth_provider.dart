import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_client/data/repositories/auth_repository.dart';
import 'package:flutter_client/models/auth_response.dart';

final authRepositoryProvider = Provider((ref) => AuthRepository());

final authStateProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  return AuthNotifier(ref.read(authRepositoryProvider));
});

class AuthState {
  final bool isLoading;
  final AuthResponse? user;
  final String? error;
  final bool isAuthenticated;

  AuthState({
    this.isLoading = false,
    this.user,
    this.error,
    this.isAuthenticated = false,
  });

  AuthState copyWith({
    bool? isLoading,
    AuthResponse? user,
    String? error,
    bool? isAuthenticated,
  }) {
    return AuthState(
      isLoading: isLoading ?? this.isLoading,
      user: user ?? this.user,
      error: error ?? this.error,
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
    );
  }
}

class AuthNotifier extends StateNotifier<AuthState> {
  final AuthRepository _authRepository;

  AuthNotifier(this._authRepository) : super(AuthState());

  Future<void> login(String email, String password) async {
    state = state.copyWith(isLoading: true, error: null);
    
    try {
      final user = await _authRepository.login(email, password);
      state = state.copyWith(
        isLoading: false,
        user: user,
        isAuthenticated: true,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  Future<void> register({
    required String nombre,
    required String email,
    required String password,
    required String confirmPassword,
    String? telefono,
  }) async {
    state = state.copyWith(isLoading: true, error: null);
    
    try {
      final user = await _authRepository.register(
        nombre: nombre,
        email: email,
        password: password,
        confirmPassword: confirmPassword,
        telefono: telefono,
      );
      state = state.copyWith(
        isLoading: false,
        user: user,
        isAuthenticated: true,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  Future<void> logout() async {
    await _authRepository.logout();
    state = AuthState();
  }

  Future<void> checkAuth() async {
    final isAuthenticated = await _authRepository.isAuthenticated();
    if (isAuthenticated) {
      final userName = await _authRepository.getUserName();
      final userEmail = await _authRepository.getUserEmail();
      
      // Simular usuario desde datos guardados
      final user = AuthResponse(
        token: '',
        refreshToken: '',
        type: 'Bearer',
        usuarioId: 0,
        email: userEmail ?? '',
        nombre: userName ?? '',
        mensaje: '',
      );
      
      state = state.copyWith(
        isAuthenticated: true,
        user: user,
      );
    }
  }
}
