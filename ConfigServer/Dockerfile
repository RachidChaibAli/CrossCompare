# Use an official OpenJDK 24 image as the base image
FROM openjdk:25-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copia el archivo JAR generado en tu máquina al contenedor
COPY target/*.jar app.jar

# Expone el puerto correcto de Spring Boot para Config Server
EXPOSE 8088

# Instala curl para el healthcheck
RUN apt-get update && apt-get install -y curl

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]