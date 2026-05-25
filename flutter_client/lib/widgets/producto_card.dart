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
    final colorScheme = Theme.of(context).colorScheme;
    final stockOk = producto.stock > 0;
    final stockColor = stockOk ? colorScheme.secondary : colorScheme.error;
    final statusBg =
        producto.activo ? colorScheme.primary.withAlpha(26) : colorScheme.error.withAlpha(26);
    final statusFg = producto.activo ? colorScheme.primary : colorScheme.error;

    return Card(
      child: InkWell(
        onTap: () {
          context.push('/producto/${producto.id}');
        },
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Container(
                width: 60,
                height: 60,
                decoration: BoxDecoration(
                  color: colorScheme.primary.withAlpha(20),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Icon(Icons.inventory_2_outlined, size: 32, color: colorScheme.primary),
              ),
              const SizedBox(width: 16),
              
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      producto.nombre,
                      style: Theme.of(context)
                          .textTheme
                          .titleMedium
                          ?.copyWith(fontWeight: FontWeight.w700),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 4),
                    if (producto.descripcion != null)
                      Text(
                        producto.descripcion!,
                        style: Theme.of(context)
                            .textTheme
                            .bodyMedium
                            ?.copyWith(color: colorScheme.onSurfaceVariant),
                        maxLines: 2,
                        overflow: TextOverflow.ellipsis,
                      ),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Text(
                          producto.precioFormateado,
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                                fontWeight: FontWeight.w800,
                                color: colorScheme.primary,
                              ),
                        ),
                        const SizedBox(width: 16),
                        Icon(
                          Icons.inventory,
                          size: 16,
                          color: stockColor,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          'Stock: ${producto.stock}',
                          style: TextStyle(
                            fontSize: 14,
                            fontWeight: FontWeight.w600,
                            color: stockColor,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: statusBg,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  producto.activo ? 'Activo' : 'Inactivo',
                  style: TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: statusFg),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
