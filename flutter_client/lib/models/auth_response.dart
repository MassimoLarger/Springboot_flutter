import 'package:json_annotation/json_annotation.dart';

part 'auth_response.g.dart';

@JsonSerializable()
class AuthResponse {
  final String token;
  final String refreshToken;
  final String type;
  final int usuarioId;
  final String email;
  final String nombre;
  final String mensaje;

  AuthResponse({
    required this.token,
    required this.refreshToken,
    required this.type,
    required this.usuarioId,
    required this.email,
    required this.nombre,
    required this.mensaje,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) =>
      _$AuthResponseFromJson(json);

  Map<String, dynamic> toJson() => _$AuthResponseToJson(this);
}