import 'package:flutter_client/data/services/api_client.dart';
import 'package:flutter_client/models/producto.dart';
import 'package:flutter_client/models/paginated_response.dart';

class ProductoRepository {
  Future<PaginatedResponse<Producto>> getProductos({
    int page = 0,
    int size = 10,
    String sort = 'nombre',
    String direction = 'asc',
  }) async {
    try {
      final response = await ApiClient.getProductos(
        page: page,
        size: size,
        sort: sort,
        direction: direction,
      );
      
      final dynamic apiResponse = response.data;
      
      if (apiResponse is Map<String, dynamic>) {
        // Caso normal: PaginatedResponse o ApiResponse<PaginatedResponse>
        if (apiResponse.containsKey('pagination')) {
          return PaginatedResponse<Producto>.fromJson(
            apiResponse,
            (json) => Producto.fromJson(json as Map<String, dynamic>),
          );
        } else if (apiResponse.containsKey('data') && apiResponse['data'] is Map) {
          return PaginatedResponse<Producto>.fromJson(
            apiResponse['data'],
            (json) => Producto.fromJson(json as Map<String, dynamic>),
          );
        }
      } else if (apiResponse is List) {
        // Si por alguna razón el backend devuelve una lista plana
        return PaginatedResponse<Producto>(
          success: true,
          data: apiResponse.map((j) => Producto.fromJson(j as Map<String, dynamic>)).toList(),
          message: 'Productos obtenidos',
          pagination: PaginationInfo(
            currentPage: 0,
            pageSize: apiResponse.length,
            totalElements: apiResponse.length,
            totalPages: 1,
            isFirst: true,
            isLast: true,
            hasNext: false,
            hasPrevious: false,
          ),
          timestamp: DateTime.now(),
        );
      }
      
      throw Exception('Formato de respuesta inesperado: $apiResponse');
    } catch (e) {
      rethrow;
    }
  }
  
  Future<PaginatedResponse<Producto>> buscarProductos({
    required String nombre,
    int page = 0,
    int size = 10,
  }) async {
    try {
      final response = await ApiClient.buscarProductos(
        nombre: nombre,
        page: page,
        size: size,
      );
      
      final dynamic apiResponse = response.data;
      
      if (apiResponse is Map<String, dynamic>) {
        if (apiResponse.containsKey('pagination')) {
          return PaginatedResponse<Producto>.fromJson(
            apiResponse,
            (json) => Producto.fromJson(json as Map<String, dynamic>),
          );
        } else if (apiResponse.containsKey('data') && apiResponse['data'] is Map) {
          return PaginatedResponse<Producto>.fromJson(
            apiResponse['data'],
            (json) => Producto.fromJson(json as Map<String, dynamic>),
          );
        }
      }
      
      throw Exception('Formato de respuesta inesperado: $apiResponse');
    } catch (e) {
      rethrow;
    }
  }
  
  Future<Producto> getProducto(int id) async {
    try {
      final response = await ApiClient.getProducto(id);
      return Producto.fromJson(response.data['data']);
    } catch (e) {
      rethrow;
    }
  }
  
  Future<Producto> createProducto({
    required String nombre,
    String? descripcion,
    required double precio,
    required int stock,
  }) async {
    try {
      final response = await ApiClient.createProducto({
        'nombre': nombre,
        'descripcion': descripcion,
        'precio': precio,
        'stock': stock,
      });
      return Producto.fromJson(response.data['data']);
    } catch (e) {
      rethrow;
    }
  }
  
  Future<Producto> updateProducto({
    required int id,
    required String nombre,
    String? descripcion,
    required double precio,
    required int stock,
  }) async {
    try {
      final response = await ApiClient.updateProducto(id, {
        'nombre': nombre,
        'descripcion': descripcion,
        'precio': precio,
        'stock': stock,
      });
      return Producto.fromJson(response.data['data']);
    } catch (e) {
      rethrow;
    }
  }
  
  Future<void> deleteProducto(int id) async {
    try {
      await ApiClient.deleteProducto(id);
    } catch (e) {
      rethrow;
    }
  }
  
  Future<void> restaurarProducto(int id) async {
    try {
      await ApiClient.restaurarProducto(id);
    } catch (e) {
      rethrow;
    }
  }
}