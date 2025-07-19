# Proyecto Backend Monolítico

Este proyecto unifica todos los microservicios previos en una sola aplicación Java Spring Boot, estructurada por capas (controller, service, repository, model, config, etc.).

## Estructura sugerida

- `controller/`: Controladores REST para la API.
- `service/`: Lógica de negocio.
- `repository/`: Acceso a datos y persistencia.
- `model/`: Entidades y modelos de datos.
- `config/`: Configuración general del proyecto.

## Ejecución local

1. Instala Java 17+ y Maven.
2. Ejecuta:
   ```bash
   mvn spring-boot:run
   ```
3. La API estará disponible en `http://localhost:8080`.

## Notas

- Aquí se migrará y adaptará la lógica de los microservicios: autenticación, documentos, eventos, etc.
- No requiere Docker ni contenedores.
