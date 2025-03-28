# Vectora Account Service Test

Este proyecto es un servicio de cuentas desarrollado en Java con Spring Boot. Se puede ejecutar en un entorno local o dentro de un contenedor Docker.

## 📋 Requisitos

Antes de ejecutar el servicio, asegúrate de tener instalados:

- [Docker](https://www.docker.com/)

## 🚀 Instalación y Ejecución


###  Ejecutar con Docker

1. **Construir la imagen Docker**
   ```bash
   docker build -t vectora-account-service .
   ```

2. **Ejecutar el contenedor**
   ```bash
   docker-compose up -d
   ```

## 🛠️ Configuración

El servicio utiliza variables de entorno para configurar su ejecución. Puedes definirlas en un archivo `.env` o pasarlas al ejecutar el contenedor.

## 📝 Endpoints

### Obtener un token de autenticación
Antes de realizar cualquier operación, es necesario obtener un token JWT con el siguiente endpoint:
```bash
curl -X POST http://localhost:8080/auth/token \
     -H "Content-Type: application/json" \
     -d '{"username": "admin", "password": "password"}'
```

Este comando devolverá un token JWT que debe incluirse en las siguientes solicitudes como `Authorization: Bearer <token>`.

### Crear una cuenta
```bash
curl -X POST http://localhost:8080/accounts \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{"name": "John Doe", "email": "john.doe@example.com"}'
```

### Obtener una cuenta por ID
```bash
curl -X GET http://localhost:8080/accounts/{id} \
     -H "Authorization: Bearer <token>"
```

### Validar una cuenta con un monto
```bash
curl -X GET "http://localhost:8080/accounts/{id}/validate?amount=100.50" \
     -H "Authorization: Bearer <token>"
```

## 📜 Licencia

Este proyecto está bajo la licencia MIT.