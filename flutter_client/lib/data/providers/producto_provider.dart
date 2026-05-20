import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_client/data/repositories/producto_repository.dart';
import 'package:flutter_client/models/producto.dart';

final productoRepositoryProvider = Provider((ref) => ProductoRepository());

final productosProvider = StateNotifierProvider<ProductosNotifier, ProductosState>((ref) {
  return ProductosNotifier(ref.read(productoRepositoryProvider));
});

class ProductosState {
  final List<Producto> productos;
  final bool isLoading;
  final String? error;
  final int currentPage;
  final int totalPages;
  final bool hasNext;
  final bool isLoadingMore;

  ProductosState({
    this.productos = const [],
    this.isLoading = false,
    this.error,
    this.currentPage = 0,
    this.totalPages = 0,
    this.hasNext = false,
    this.isLoadingMore = false,
  });

  ProductosState copyWith({
    List<Producto>? productos,
    bool? isLoading,
    String? error,
    int? currentPage,
    int? totalPages,
    bool? hasNext,
    bool? isLoadingMore,
  }) {
    return ProductosState(
      productos: productos ?? this.productos,
      isLoading: isLoading ?? this.isLoading,
      error: error ?? this.error,
      currentPage: currentPage ?? this.currentPage,
      totalPages: totalPages ?? this.totalPages,
      hasNext: hasNext ?? this.hasNext,
      isLoadingMore: isLoadingMore ?? this.isLoadingMore,
    );
  }
}

class ProductosNotifier extends StateNotifier<ProductosState> {
  final ProductoRepository _productoRepository;

  ProductosNotifier(this._productoRepository) : super(ProductosState());

  Future<void> loadProductos({bool refresh = false}) async {
    await Future.microtask(() {});

    if (refresh) {
      state = state.copyWith(isLoading: true, productos: [], currentPage: 0);
    } else if (state.currentPage == 0) {
      state = state.copyWith(isLoading: true);
    }

    try {
      final result = await _productoRepository.getProductos(
        page: refresh ? 0 : state.currentPage,
        size: 10,
      );

      final nuevosProductos = refresh 
          ? result.data 
          : [...state.productos, ...result.data];

      state = state.copyWith(
        productos: nuevosProductos,
        isLoading: false,
        currentPage: result.pagination.currentPage + 1,
        totalPages: result.pagination.totalPages,
        hasNext: result.pagination.hasNext,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  Future<void> loadMore() async {
    if (!state.hasNext || state.isLoadingMore) return;

    state = state.copyWith(isLoadingMore: true);

    try {
      final result = await _productoRepository.getProductos(
        page: state.currentPage,
        size: 10,
      );

      state = state.copyWith(
        productos: [...state.productos, ...result.data],
        isLoadingMore: false,
        currentPage: result.pagination.currentPage + 1,
        totalPages: result.pagination.totalPages,
        hasNext: result.pagination.hasNext,
      );
    } catch (e) {
      state = state.copyWith(
        isLoadingMore: false,
        error: e.toString(),
      );
    }
  }

  Future<void> buscarProductos(String nombre) async {
    if (nombre.isEmpty) {
      await loadProductos(refresh: true);
      return;
    }

    state = state.copyWith(isLoading: true, productos: [], currentPage: 0, error: null);

    try {
      final result = await _productoRepository.buscarProductos(
        nombre: nombre,
        page: 0,
        size: 20,
      );

      state = state.copyWith(
        productos: result.data,
        isLoading: false,
        currentPage: result.pagination.currentPage + 1,
        totalPages: result.pagination.totalPages,
        hasNext: result.pagination.hasNext,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  Future<void> createProducto({
    required String nombre,
    String? descripcion,
    required double precio,
    required int stock,
  }) async {
    try {
      final nuevoProducto = await _productoRepository.createProducto(
        nombre: nombre,
        descripcion: descripcion,
        precio: precio,
        stock: stock,
      );
      
      state = state.copyWith(
        productos: [nuevoProducto, ...state.productos],
      );
    } catch (e) {
      rethrow;
    }
  }

  Future<void> updateProducto({
    required int id,
    required String nombre,
    String? descripcion,
    required double precio,
    required int stock,
  }) async {
    try {
      final productoActualizado = await _productoRepository.updateProducto(
        id: id,
        nombre: nombre,
        descripcion: descripcion,
        precio: precio,
        stock: stock,
      );
      
      final index = state.productos.indexWhere((p) => p.id == id);
      if (index != -1) {
        final nuevosProductos = List<Producto>.from(state.productos);
        nuevosProductos[index] = productoActualizado;
        state = state.copyWith(productos: nuevosProductos);
      }
    } catch (e) {
      rethrow;
    }
  }

  Future<void> deleteProducto(int id) async {
    try {
      await _productoRepository.deleteProducto(id);
      state = state.copyWith(
        productos: state.productos.where((p) => p.id != id).toList(),
      );
    } catch (e) {
      rethrow;
    }
  }
}
