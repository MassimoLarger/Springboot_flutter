import 'package:json_annotation/json_annotation.dart';

part 'register_request.g.dart';

@JsonSerializable()
class RegisterRequest {
  final String nombre;
  final String email;
  final String password;
  final String confirmPassword;
  final String? telefono;

  RegisterRequest({
    required this.nombre,
    required this.email,
    required this.password,
    required this.confirmPassword,
    this.telefono,
  });

  factory RegisterRequest.fromJson(Map<String, dynamic> json) =>
      _$RegisterRequestFromJson(json);

  Map<String, dynamic> toJson() => _$RegisterRequestToJson(this);
}
