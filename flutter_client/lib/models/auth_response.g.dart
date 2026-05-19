// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'auth_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AuthResponse _$AuthResponseFromJson(Map<String, dynamic> json) => AuthResponse(
      token: json['token'] as String,
      refreshToken: json['refreshToken'] as String,
      type: json['type'] as String,
      usuarioId: (json['usuarioId'] as num).toInt(),
      email: json['email'] as String,
      nombre: json['nombre'] as String,
      mensaje: json['mensaje'] as String,
    );

Map<String, dynamic> _$AuthResponseToJson(AuthResponse instance) =>
    <String, dynamic>{
      'token': instance.token,
      'refreshToken': instance.refreshToken,
      'type': instance.type,
      'usuarioId': instance.usuarioId,
      'email': instance.email,
      'nombre': instance.nombre,
      'mensaje': instance.mensaje,
    };
