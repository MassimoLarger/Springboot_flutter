// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'producto_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ProductoRequest _$ProductoRequestFromJson(Map<String, dynamic> json) =>
    ProductoRequest(
      nombre: json['nombre'] as String,
      descripcion: json['descripcion'] as String?,
      precio: (json['precio'] as num).toDouble(),
      stock: (json['stock'] as num).toInt(),
    );

Map<String, dynamic> _$ProductoRequestToJson(ProductoRequest instance) =>
    <String, dynamic>{
      'nombre': instance.nombre,
      'descripcion': instance.descripcion,
      'precio': instance.precio,
      'stock': instance.stock,
    };
