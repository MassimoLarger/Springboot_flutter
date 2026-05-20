import 'package:flutter/material.dart';
import 'package:flutter_client/models/producto.dart';
import 'package:go_router/go_router.dart';

class ProductoCard extends StatelessWidget {
  final Producto producto;

  const ProductoCard({
    super.key,
    required this.producto,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: InkWell(
        onTap: () {
          context.go('/producto/${producto.id}');
        },
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              // Icono
              Container(
                width: 60,
                height: 60,
                decoration: BoxDecoration(
                  color: Theme.of(context).primaryColor.withAlpha(26),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Icon(Icons.inventory_2, size: 32),
              ),
              const SizedBox(width: 16),
              
              // Información
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      producto.nombre,
                      style: const TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 4),
                    if (producto.descripcion != null)
                      Text(
                        producto.descripcion!,
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.grey[600],
                        ),
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis,
                      ),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Text(
                          producto.precioFormateado,
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: Theme.of(context).primaryColor,
                          ),
                        ),
                        const SizedBox(width: 16),
                        Icon(
                          Icons.inventory,
                          size: 16,
                          color: producto.stock > 0 ? Colors.green : Colors.red,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          'Stock: ${producto.stock}',
                          style: TextStyle(
                            fontSize: 14,
                            color: producto.stock > 0 ? Colors.green : Colors.red,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              
              // Estado
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: producto.activo ? Colors.green : Colors.red,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  producto.activo ? 'Activo' : 'Inactivo',
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 12,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
