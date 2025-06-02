# CrossCompare

[![Deploy Status](https://img.shields.io/badge/deploy-Google%20Cloud-blue?logo=googlecloud)](https://console.cloud.google.com/cloud-build/builds?project=onyx-messenger-461611-t7)

CrossCompare es una aplicación web para la comparación de precios de videojuegos en distintas plataformas. Está desarrollada con una arquitectura de microservicios basada en Spring Boot (backend) y una interfaz de usuario moderna en React (frontend). El sistema recopila y unifica información de precios desde diferentes fuentes y la presenta al usuario final.

**Proyecto online:** [crosscompare.xyz](https://crosscompare.xyz)

**Código fuente:** [GitHub](https://github.com/RachidChaibAli/CrossCompare)

---

## Arquitectura

El sistema sigue el modelo de vistas 4+1, que permite una visión clara y estructurada del funcionamiento y despliegue:

### Microservicios principales

- **Frontend:** Aplicación React para interacción con el usuario.
- **Unificador:** Orquesta la recolección y unificación de datos de precios.
- **Recolectores:** Microservicios independientes (web scrapers/API consumers) para cada plataforma.
- **API Gateway:** Punto de entrada único al sistema.
- **Eureka:** Descubrimiento de servicios.
- **Config Server:** Centraliza la configuración de los servicios.

### Bases de datos

- **Unificador:** Guarda información de juegos y precios históricos.

### Comunicación y procesos

- El frontend realiza llamadas a la API Gateway.
- El Unificador gestiona peticiones y coordina la recolección de datos a través de mensajes (usando Redis Streams) entre los recolectores y el propio Unificador.
- **Solo el frontend y API Gateway están expuestos públicamente**; el resto de servicios y bases de datos están en una red privada de Docker.

---

## Despliegue

El proyecto está desplegado en una máquina virtual de Google Cloud Platform usando Docker Compose para orquestar todos los servicios. El despliegue continuo (CI/CD) se realiza con Google Cloud Build.

### Pipeline de despliegue

1. Compilación de los .jar
2. Construcción de imágenes Docker para cada microservicio y frontend.
3. Push a Docker Hub (registro público).
4. Despliegue automático en la VM mediante Cloud Build, que ejecuta:

```bash
docker compose down
docker image prune -af
docker compose up -d --build
```

- **Dominio personalizado:** crosscompare.xyz apunta a la IP pública estática de la VM.

---

## Requisitos para desplegar

- Docker y Docker Compose instalados en la VM.
- Acceso a Google Cloud y permisos para usar Cloud Build y Secret Manager.
- Variables de entorno y secretos gestionados de forma segura (por ejemplo, credenciales de Docker Hub).

---

## Funcionalidades principales

- Búsqueda y comparación de precios de videojuegos en diferentes plataformas.
- Recolección automática de datos mediante scrapers y APIs.
- Interfaz web moderna y responsiva.

---

## Ejecución local

```bash
git clone https://github.com/RachidChaibAli/CrossCompare.git
cd CrossCompare
./mvn.sh # (JDK 24)
docker compose up -d --build
```

Asegúrate de tener configurados los archivos `.env` necesarios para cada servicio.

---

## Créditos

Desarrollado por [Rachid Chaib Ali](https://www.linkedin.com/in/rachid-chaib-ali-b93b462a4/).
