import 'package:dio/dio.dart';
import 'package:flutter_client/data/services/dio_config.dart';

class ApiClient {
  static final Dio _dio = DioConfig.createDio();
  
  // Auth endpoints
  static Future<Response> login(Map<String, dynamic> data) async {
    return await _dio.post('/auth/login', data: data);
  }
  
  static Future<Response> register(Map<String, dynamic> data) async {
    return await _dio.post('/auth/register', data: data);
  }
  
  static Future<Response> refreshToken(Map<String, dynamic> data) async {
    return await _dio.post('/auth/refresh', data: data);
  }
  
  static Future<Response> logout(Map<String, dynamic> data) async {
    return await _dio.post('/auth/logout', data: data);
  }
  
  // Product endpoints
  static Future<Response> getProductos({
    int page = 0,
    int size = 10,
    String sort = 'nombre',
    String direction = 'asc',
  }) async {
    return await _dio.get(
      '/productos',
      queryParameters: {
        'page': page,
        'size': size,
        'sort': sort,
        'direction': direction,
      },
    );
  }
  
  static Future<Response> buscarProductos({
    required String nombre,
    int page = 0,
    int size = 10,
  }) async {
    return await _dio.get(
      '/productos/buscar',
      queryParameters: {
        'nombre': nombre,
        'page': page,
        'size': size,
      },
    );
  }
  
  static Future<Response> getProducto(int id) async {
    return await _dio.get('/productos/$id');
  }
  
  static Future<Response> createProducto(Map<String, dynamic> data) async {
    return await _dio.post('/productos', data: data);
  }
  
  static Future<Response> updateProducto(int id, Map<String, dynamic> data) async {
    return await _dio.put('/productos/$id', data: data);
  }
  
  static Future<Response> deleteProducto(int id) async {
    return await _dio.delete('/productos/$id');
  }
  
  static Future<Response> restaurarProducto(int id) async {
    return await _dio.patch('/productos/$id/restaurar');
  }
}
