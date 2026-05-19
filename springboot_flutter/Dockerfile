# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Instalar curl para healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Crear usuario no root por seguridad
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copiar JAR desde stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Cambiar a usuario no root
USER appuser

# Exponer puerto
EXPOSE 8080

# Health check - verifica el endpoint real de la app
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Ejecutar aplicación con optimizaciones JVM
ENTRYPOINT ["java", \
  "-Xms256m", \
  "-Xmx512m", \
  "-XX:+UseG1GC", \
  "-XX:+UseContainerSupport", \
  "-jar", \
  "app.jar"]