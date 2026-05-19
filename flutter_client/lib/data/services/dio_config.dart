import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class DioConfig {
  static const String baseUrl = 'http://localhost:8080';
  
  static Dio createDio() {
    final dio = Dio(BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    ));
    
    // Interceptor para logging
    dio.interceptors.add(LogInterceptor(
      requestBody: true,
      responseBody: true,
      requestHeader: true,
    ));
    
    // Interceptor para token JWT
    dio.interceptors.add(AuthInterceptor());
    
    return dio;
  }
}

class AuthInterceptor extends Interceptor {
  final FlutterSecureStorage _storage = const FlutterSecureStorage();
  
  @override
  Future<void> onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    final token = await _storage.read(key: 'access_token');
    
    if (token != null && token.isNotEmpty) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    
    return handler.next(options);
  }
  
  @override
  Future<void> onError(
    DioException err,
    ErrorInterceptorHandler handler,
  ) async {
    if (err.response?.statusCode == 401) {
      // Token expirado - intentar refrescar
      final refreshToken = await _storage.read(key: 'refresh_token');
      
      if (refreshToken != null) {
        try {
          final dio = Dio(BaseOptions(baseUrl: DioConfig.baseUrl));
          final response = await dio.post(
            '/auth/refresh',
            data: {'refreshToken': refreshToken},
          );
          
          if (response.statusCode == 200) {
            final newToken = response.data['data']['accessToken'];
            final newRefreshToken = response.data['data']['refreshToken'];
            
            await _storage.write(key: 'access_token', value: newToken);
            await _storage.write(key: 'refresh_token', value: newRefreshToken);
            
            // Reintentar petición original
            final newOptions = err.requestOptions;
            newOptions.headers['Authorization'] = 'Bearer $newToken';
            
            final retryResponse = await dio.fetch(newOptions);
            return handler.resolve(retryResponse);
          }
        } catch (e) {
          // Refresh falló - limpiar y redirigir a login
          await _storage.deleteAll();
        }
      }
    }
    
    return handler.next(err);
  }
}
