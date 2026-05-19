import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_client/data/providers/auth_provider.dart';
import 'package:flutter_client/data/providers/producto_provider.dart';
import 'package:flutter_client/widgets/producto_card.dart';
import 'package:flutter_client/widgets/loading_widget.dart';
import 'package:go_router/go_router.dart';

class HomeScreen extends ConsumerStatefulWidget {
  const HomeScreen({super.key});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen> {
  final _scrollController = ScrollController();
  final _searchController = TextEditingController();
  bool _isSearching = false;

  @override
  void initState() {
    super.initState();
    _loadInitialData();
    _scrollController.addListener(_onScroll);
  }

  Future<void> _loadInitialData() async {
    await ref.read(productosProvider.notifier).loadProductos(refresh: true);
  }

  void _onScroll() {
    if (_scrollController.position.pixels >= 
        _scrollController.position.maxScrollExtent - 200) {
      ref.read(productosProvider.notifier).loadMore();
    }
  }

  Future<void> _logout() async {
    await ref.read(authStateProvider.notifier).logout();
    if (mounted) {
      context.go('/login');
    }
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final productosState = ref.watch(productosProvider);
    final authState = ref.watch(authStateProvider);

    return Scaffold(
      appBar: AppBar(
        title: _isSearching
            ? TextField(
                controller: _searchController,
                autofocus: true,
                decoration: const InputDecoration(
                  hintText: 'Buscar productos...',
                  border: InputBorder.none,
                ),
                onSubmitted: (value) {
                  // Implementar búsqueda
                },
              )
            : const Text('Mis Productos'),
        actions: [
          IconButton(
            icon: Icon(_isSearching ? Icons.close : Icons.search),
            onPressed: () {
              setState(() {
                _isSearching = !_isSearching;
                if (!_isSearching) {
                  _searchController.clear();
                  _loadInitialData();
                }
              });
            },
          ),
          PopupMenuButton(
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: 'profile',
                child: Text('Mi Perfil'),
              ),
              const PopupMenuItem(
                value: 'logout',
                child: Text('Cerrar Sesión'),
              ),
            ],
            onSelected: (value) {
              if (value == 'logout') {
                _logout();
              }
            },
          ),
        ],
      ),
      body: _buildBody(productosState),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          context.go('/producto/nuevo');
        },
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildBody(ProductosState state) {
    if (state.isLoading && state.productos.isEmpty) {
      return const Center(child: LoadingWidget());
    }

    if (state.error != null && state.productos.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 64, color: Colors.red),
            const SizedBox(height: 16),
            Text(state.error!),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () => _loadInitialData(),
              child: const Text('Reintentar'),
            ),
          ],
        ),
      );
    }

    if (state.productos.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.inventory_2_outlined, size: 64, color: Colors.grey),
            SizedBox(height: 16),
            Text('No hay productos'),
            SizedBox(height: 8),
            Text('Presiona + para agregar'),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: () => ref.read(productosProvider.notifier).loadProductos(refresh: true),
      child: ListView.builder(
        controller: _scrollController,
        itemCount: state.productos.length + (state.isLoadingMore ? 1 : 0),
        itemBuilder: (context, index) {
          if (index == state.productos.length) {
            return const Padding(
              padding: EdgeInsets.all(16),
              child: Center(child: CircularProgressIndicator()),
            );
          }
          
          final producto = state.productos[index];
          return ProductoCard(producto: producto);
        },
      ),
    );
  }
}
