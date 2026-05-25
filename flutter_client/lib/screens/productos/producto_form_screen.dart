import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_client/data/providers/producto_provider.dart';
import 'package:flutter_client/widgets/loading_widget.dart';
import 'package:go_router/go_router.dart';

class ProductoFormScreen extends ConsumerStatefulWidget {
  final int? productoId;

  const ProductoFormScreen({super.key, this.productoId});

  @override
  ConsumerState<ProductoFormScreen> createState() => _ProductoFormScreenState();
}

class _ProductoFormScreenState extends ConsumerState<ProductoFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nombreController = TextEditingController();
  final _descripcionController = TextEditingController();
  final _precioController = TextEditingController();
  final _stockController = TextEditingController();
  
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    if (widget.productoId != null) {
      _cargarProducto();
    }
  }

  Future<void> _cargarProducto() async {
    setState(() => _isLoading = true);
    
    try {
      final repository = ref.read(productoRepositoryProvider);
      final producto = await repository.getProducto(widget.productoId!);
      
      _nombreController.text = producto.nombre;
      _descripcionController.text = producto.descripcion ?? '';
      _precioController.text = producto.precio.toString();
      _stockController.text = producto.stock.toString();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: $e')),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Future<void> _guardar() async {
    if (_formKey.currentState!.validate()) {
      setState(() => _isLoading = true);
      
      try {
        final notifier = ref.read(productosProvider.notifier);
        final nombre = _nombreController.text.trim();
        final descripcion = _descripcionController.text.trim();
        final precio = double.parse(_precioController.text);
        final stock = int.parse(_stockController.text);
        
        if (widget.productoId == null) {
          await notifier.createProducto(
            nombre: nombre,
            descripcion: descripcion.isEmpty ? null : descripcion,
            precio: precio,
            stock: stock,
          );
          
          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Producto creado exitosamente'),
                backgroundColor: Colors.green,
              ),
            );
          }
        } else {
          await notifier.updateProducto(
            id: widget.productoId!,
            nombre: nombre,
            descripcion: descripcion.isEmpty ? null : descripcion,
            precio: precio,
            stock: stock,
          );
          
          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Producto actualizado exitosamente'),
                backgroundColor: Colors.green,
              ),
            );
          }
        }
        
        if (mounted) {
          if (Navigator.of(context).canPop()) {
            Navigator.of(context).pop();
          } else {
            context.go('/home');
          }
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Error: $e'),
              backgroundColor: Colors.red,
            ),
          );
        }
      } finally {
        if (mounted) {
          setState(() => _isLoading = false);
        }
      }
    }
  }

  @override
  void dispose() {
    _nombreController.dispose();
    _descripcionController.dispose();
    _precioController.dispose();
    _stockController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final titulo = widget.productoId == null ? 'Nuevo Producto' : 'Editar Producto';

    return Scaffold(
      appBar: AppBar(
        title: Text(titulo),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            if (Navigator.of(context).canPop()) {
              Navigator.of(context).pop();
            } else {
              context.go('/home');
            }
          },
          tooltip: 'Volver',
        ),
      ),
      body: _isLoading
          ? const LoadingWidget()
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Form(
                key: _formKey,
                child: Column(
                  children: [
                    TextFormField(
                      controller: _nombreController,
                      decoration: const InputDecoration(
                        labelText: 'Nombre del producto',
                        prefixIcon: Icon(Icons.sell_outlined),
                      ),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return 'El nombre es requerido';
                        }
                        if (value.length < 3) {
                          return 'El nombre debe tener al menos 3 caracteres';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    
                    TextFormField(
                      controller: _descripcionController,
                      decoration: const InputDecoration(
                        labelText: 'Descripción (opcional)',
                        prefixIcon: Icon(Icons.description_outlined),
                      ),
                      maxLines: 3,
                    ),
                    const SizedBox(height: 16),
                    
                    TextFormField(
                      controller: _precioController,
                      decoration: const InputDecoration(
                        labelText: 'Precio',
                        prefixIcon: Icon(Icons.payments_outlined),
                      ),
                      keyboardType: const TextInputType.numberWithOptions(decimal: true),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return 'El precio es requerido';
                        }
                        final precio = double.tryParse(value);
                        if (precio == null || precio <= 0) {
                          return 'El precio debe ser mayor a 0';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    
                    TextFormField(
                      controller: _stockController,
                      decoration: const InputDecoration(
                        labelText: 'Stock',
                        prefixIcon: Icon(Icons.inventory_2_outlined),
                      ),
                      keyboardType: TextInputType.number,
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return 'El stock es requerido';
                        }
                        final stock = int.tryParse(value);
                        if (stock == null || stock < 0) {
                          return 'El stock no puede ser negativo';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 32),
                    
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton(
                        onPressed: _guardar,
                        child: Text(widget.productoId == null ? 'Crear' : 'Actualizar'),
                      ),
                    ),
                  ],
                ),
              ),
            ),
    );
  }
}
