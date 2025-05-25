# Use an official OpenJDK 24 image as the base image
FROM openjdk:25-jdk-slim

# Instala dependencias necesarias
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    wget unzip xvfb chromium chromium-driver curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* && \
    rm -rf /usr/share/doc /usr/share/man /usr/share/locale

# Set the working directory in the container
WORKDIR /app

# Copia el archivo JAR generado en tu máquina al contenedor
COPY target/*.jar app.jar

# Expone el puerto por defecto de Spring Boot (ajusta si usas otro)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
