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
      
      // La respuesta de la API viene envuelta en ApiResponse
      final apiResponse = response.data;
      final paginatedData = apiResponse['data'];
      
      return PaginatedResponse<Producto>.fromJson(
        paginatedData,
        (json) => Producto.fromJson(json as Map<String, dynamic>),
      );
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
      
      final apiResponse = response.data;
      final paginatedData = apiResponse['data'];
      
      return PaginatedResponse<Producto>.fromJson(
        paginatedData,
        (json) => Producto.fromJson(json as Map<String, dynamic>),
      );
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