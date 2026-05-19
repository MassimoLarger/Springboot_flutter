import 'package:json_annotation/json_annotation.dart';

part 'producto_request.g.dart';

@JsonSerializable()
class ProductoRequest {
  final String nombre;
  final String? descripcion;
  final double precio;
  final int stock;

  ProductoRequest({
    required this.nombre,
    this.descripcion,
    required this.precio,
    required this.stock,
  });

  factory ProductoRequest.fromJson(Map<String, dynamic> json) =>
      _$ProductoRequestFromJson(json);

  Map<String, dynamic> toJson() => _$ProductoRequestToJson(this);
}
