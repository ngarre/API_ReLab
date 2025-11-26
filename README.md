# API_ReLab

API REST construida con Java, Spring Boot y Maven que se conecta a una base de datos relacional.

## Tecnologías
- Java 17+
- Spring Boot
- Maven
- Base de datos relacional (MariaDB/MySQL)
- (Opcional) springdoc-openapi / Swagger para documentación

## Requisitos previos
- JDK 17+ instalado
- Maven 3.6+ instalado
- Base de datos MariaDB/MySQL accesible
- IntelliJ IDEA (opcional, versión 2025.2.3 indicada)

## Configuración
1. Copiar y adaptar `src/main/resources/application.properties` con los datos de tu base de datos:
   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3306/relab
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   spring.jpa.hibernate.ddl-auto=update
    ```

2. La aplicación creará automáticamente las tablas necesarias si no existen.

## Ejecución

* Para correr la aplicación:

  ```bash
  mvn spring-boot:run
  ```
* Para ejecutar los tests:

  ```bash
  mvn test
  ```
* La API estará disponible en: `http://localhost:8080`

## Documentación de la API

* Archivo OpenAPI 3.0 disponible en `openapi.yaml`
* Se puede usar Swagger UI para probar los endpoints de manera interactiva (opcional).

## Logging

* Configurado en `src/main/resources/logback-spring.xml`
* Logs registran requests, validaciones y errores.
* Se pueden visualizar en consola o en archivos bajo la carpeta `logs`.
* Logs antiguos se archivan automáticamente con rotación diaria y límite de 30 días.

## Endpoints principales

* **Usuarios**: CRUD de usuarios con filtrado y validaciones.
* **Productos**: CRUD de productos, con soporte para recuperar, subir y actualizar imágenes.
* **Categorías**: CRUD de categorías con filtrado y validaciones.
* **Compraventas**: CRUD de compraventas con filtrado y validaciones.
* **Alquileres**: CRUD de alquileres con filtrado y validaciones.

## Pruebas y Mocks

* Carpeta `wiremock` con `files` y `mappings` para simulaciones de endpoints.
* Para levantar WireMock:
    1. Abrir terminal y situarse en la carpeta `wiremock`:
       ```bash
       cd wiremock
       ```
    2. Ejecutar WireMock:
       ```bash
       java -jar wiremock-standalone-3.13.2.jar
       ```
* Colección Postman incluida para probar todos los endpoints de ejemplo.


## Notas

* La aplicación genera y actualiza automáticamente las tablas en la base de datos según los modelos.
* Logging configurado para diferenciar niveles (`INFO`, `WARN`, `ERROR`, `DEBUG`) según necesidades de desarrollo.

## Autora
- Natalia Garré
- Estudiante 2º curso Desarrollo de Aplicaciones Multiplataforma
- Proyecto realizado como parte de la asignatura de Acceso a Datos








