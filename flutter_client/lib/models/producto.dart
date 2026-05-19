import 'package:json_annotation/json_annotation.dart';

part 'producto.g.dart';

@JsonSerializable()
class Producto {
  final int id;
  final String nombre;
  final String? descripcion;
  final double precio;
  final int stock;
  final bool activo;
  
  @JsonKey(name: 'createdAt')
  final DateTime createdAt;
  
  @JsonKey(name: 'updatedAt')
  final DateTime updatedAt;

  Producto({
    required this.id,
    required this.nombre,
    this.descripcion,
    required this.precio,
    required this.stock,
    required this.activo,
    required this.createdAt,
    required this.updatedAt,
  });

  factory Producto.fromJson(Map<String, dynamic> json) =>
      _$ProductoFromJson(json);

  Map<String, dynamic> toJson() => _$ProductoToJson(this);
  
  String get precioFormateado => '\$${precio.toStringAsFixed(2)}';
  String get fechaCreacion => '${createdAt.day}/${createdAt.month}/${createdAt.year}';
}
