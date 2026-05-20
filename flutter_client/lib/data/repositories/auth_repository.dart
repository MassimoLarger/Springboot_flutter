import 'package:flutter_client/data/services/api_client.dart';
import 'package:flutter_client/models/auth_response.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AuthRepository {
  Future<SharedPreferences> get _prefs => SharedPreferences.getInstance();
  
  Future<AuthResponse> login(String email, String password) async {
    try {
      final response = await ApiClient.login({
        'email': email,
        'password': password,
      });
      
      final data = response.data['data'];
      final authResponse = AuthResponse.fromJson(data);
      
      final prefs = await _prefs;
      await prefs.setString('access_token', authResponse.token);
      await prefs.setString('refresh_token', authResponse.refreshToken);
      await prefs.setString('user_email', authResponse.email);
      await prefs.setString('user_name', authResponse.nombre);
      await prefs.setString('user_id', authResponse.usuarioId.toString());
      
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
      
      final prefs = await _prefs;
      await prefs.setString('access_token', authResponse.token);
      await prefs.setString('refresh_token', authResponse.refreshToken);
      await prefs.setString('user_email', authResponse.email);
      await prefs.setString('user_name', authResponse.nombre);
      await prefs.setString('user_id', authResponse.usuarioId.toString());
      
      return authResponse;
    } catch (e) {
      rethrow;
    }
  }
  
  Future<void> logout() async {
    try {
      final prefs = await _prefs;
      final refreshToken = prefs.getString('refresh_token');
      if (refreshToken != null) {
        await ApiClient.logout({'refreshToken': refreshToken});
      }
    } finally {
      final prefs = await _prefs;
      await prefs.remove('access_token');
      await prefs.remove('refresh_token');
      await prefs.remove('user_email');
      await prefs.remove('user_name');
      await prefs.remove('user_id');
    }
  }
  
  Future<bool> isAuthenticated() async {
    final prefs = await _prefs;
    final token = prefs.getString('access_token');
    return token != null && token.isNotEmpty;
  }
  
  Future<String?> getUserName() async {
    final prefs = await _prefs;
    return prefs.getString('user_name');
  }
  
  Future<String?> getUserEmail() async {
    final prefs = await _prefs;
    return prefs.getString('user_email');
  }

  Future<String?> getUserId() async {
    final prefs = await _prefs;
    return prefs.getString('user_id');
  }
}
