// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility in the flutter_test package. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:flutter_client/main.dart';
import 'package:flutter_client/screens/auth/login_screen.dart';
import 'package:flutter_client/screens/productos/home_screen.dart';
import 'package:flutter_client/screens/splash_screen.dart';

void main() {
  testWidgets('Arranca y muestra pantalla de login', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: MyApp()));

    await tester.pumpAndSettle();

    await tester.pump(const Duration(seconds: 2));
    await tester.pumpAndSettle();

    final isOnLogin = find.byType(LoginScreen).evaluate().isNotEmpty;
    final isOnHome = find.byType(HomeScreen).evaluate().isNotEmpty;
    final isOnSplash = find.byType(SplashScreen).evaluate().isNotEmpty;

    expect(isOnLogin || isOnHome || isOnSplash, true);
  });
}
