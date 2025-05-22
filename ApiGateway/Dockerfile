# Use an official OpenJDK 24 image as the base image
FROM openjdk:25-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copia el archivo JAR generado en tu máquina al contenedor
COPY target/*.jar app.jar

# Expone el puerto por defecto de Spring Boot (ajusta si usas otro)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]