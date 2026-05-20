import 'package:dio/dio.dart';
import 'package:shared_preferences/shared_preferences.dart';

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
  Future<SharedPreferences> get _prefs => SharedPreferences.getInstance();
  
  @override
  Future<void> onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    final prefs = await _prefs;
    final token = prefs.getString('access_token');
    
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
    final requestPath = err.requestOptions.path;
    if (err.response?.statusCode == 401) {
      if (requestPath.contains('/auth/login') ||
          requestPath.contains('/auth/register') ||
          requestPath.contains('/auth/refresh')) {
        return handler.next(err);
      }

      final prefs = await _prefs;
      final refreshToken = prefs.getString('refresh_token');
      
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
            
            await prefs.setString('access_token', newToken);
            await prefs.setString('refresh_token', newRefreshToken);
            
            final newOptions = err.requestOptions;
            newOptions.headers['Authorization'] = 'Bearer $newToken';
            
            final retryResponse = await dio.fetch(newOptions);
            return handler.resolve(retryResponse);
          }
        } catch (e) {
          await prefs.remove('access_token');
          await prefs.remove('refresh_token');
          await prefs.remove('user_email');
          await prefs.remove('user_name');
          await prefs.remove('user_id');
        }
      }
    }
    
    return handler.next(err);
  }
}
