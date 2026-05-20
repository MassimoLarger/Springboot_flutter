import 'package:flutter_client/data/services/api_client.dart';
import 'package:flutter_client/models/auth_response.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AuthRepository {
  final FlutterSecureStorage _storage = const FlutterSecureStorage();
  
  Future<AuthResponse> login(String email, String password) async {
    try {
      final response = await ApiClient.login({
        'email': email,
        'password': password,
      });
      
      final data = response.data['data'];
      final authResponse = AuthResponse.fromJson(data);
      
      // Guardar tokens
      await _storage.write(key: 'access_token', value: authResponse.token);
      await _storage.write(key: 'refresh_token', value: authResponse.refreshToken);
      await _storage.write(key: 'user_email', value: authResponse.email);
      await _storage.write(key: 'user_name', value: authResponse.nombre);
      await _storage.write(key: 'user_id', value: authResponse.usuarioId.toString());
      
      return authResponse;
    } catch (e) {
      rethrow;
    }
  }
  
  Future<AuthResponse> register({
    required String nombre,
    required String email,
    required String password,
    required String confirmPassword,
    String? telefono,
  }) async {
    try {
      final response = await ApiClient.register({
        'nombre': nombre,
        'email': email,
        'password': password,
        'confirmPassword': confirmPassword,
        'telefono': telefono,
      });
      
      final data = response.data['data'];
      final authResponse = AuthResponse.fromJson(data);
      
      // Guardar tokens
      await _storage.write(key: 'access_token', value: authResponse.token);
      await _storage.write(key: 'refresh_token', value: authResponse.refreshToken);
      await _storage.write(key: 'user_email', value: authResponse.email);
      await _storage.write(key: 'user_name', value: authResponse.nombre);
      await _storage.write(key: 'user_id', value: authResponse.usuarioId.toString());
      
      return authResponse;
    } catch (e) {
      rethrow;
    }
  }
  
  Future<void> logout() async {
    try {
      final refreshToken = await _storage.read(key: 'refresh_token');
      if (refreshToken != null) {
        await ApiClient.logout({'refreshToken': refreshToken});
      }
    } finally {
      await _storage.deleteAll();
    }
  }
  
  Future<bool> isAuthenticated() async {
    final token = await _storage.read(key: 'access_token');
    return token != null && token.isNotEmpty;
  }
  
  Future<String?> getUserName() async {
    return await _storage.read(key: 'user_name');
  }
  
  Future<String?> getUserEmail() async {
    return await _storage.read(key: 'user_email');
  }

  Future<String?> getUserId() async {
    return await _storage.read(key: 'user_id');
  }
}
