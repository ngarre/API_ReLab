# API ReLab

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-green)
![Spring Security](https://img.shields.io/badge/Security-SpringSecurity-darkgreen)
![JWT](https://img.shields.io/badge/Auth-JWT-blue)
![Maven](https://img.shields.io/badge/Build-Maven-red)
![MariaDB](https://img.shields.io/badge/Database-MariaDB-lightgrey)
![REST API](https://img.shields.io/badge/API-REST-informational)
![License](https://img.shields.io/badge/License-Academic-lightblue)

API REST construida con Java y Spring Boot que gestiona usuarios, productos, categorías, compraventas, alquileres, servicios y valoraciones.
La aplicación se conecta a una base de datos relacional (MariaDB/MySQL) para almacenar y recuperar información.

El proyecto implementa autenticación mediante JWT (JSON Web Tokens) para proteger operaciones sensibles relacionadas con los usuarios.

## Tecnologías
- Java 17+
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- Maven
- Base de datos relacional (MariaDB/MySQL)
- ModelMapper
- SLF4J + Logback
- OpenAPI / Swagger

## Arquitectura del proyecto
- `src/main/java/com/natalia/relab`: Código fuente principal
  - `controller`: Controladores REST para manejar solicitudes HTTP
  - `service`: Lógica de negocio y servicios de la aplicación
  - `repository`: Interfaces para acceso a datos (Spring Data JPA)
  - `model`: Entidades JPA que representan las tablas de la base de datos
  - `dto`: Objetos de transferencia de datos para comunicación entre capas
  - `config`: Configuraciones de seguridad, JWT, ModelMapper, etc.
  - `exception`: Clases para manejo de excepciones personalizadas
  - `security`: Clases relacionadas con la seguridad y autenticación JWT
- `resources`:
  - `application.properties`: Configuración de la aplicación (base de datos)
  - `logback-spring.xml`: Configuración de logging
  - `openapi.yaml`: Especificación OpenAPI 3.0 para la documentación de la API
- `wiremock`: Carpeta para simulaciones de endpoints con WireMock
- `postman_collection.json`: Colección de Postman para probar los endpoints de la API
- `pom.xml`: Archivo de configuración de Maven con dependencias y plugins necesarios para el proyecto

## Autenticación y seguridad (JWT)
La API utiliza JWT (JSON Web Token) para autenticar operaciones sensibles sobre usuarios.

### Flujo de autenticación
1. El cliente envía una solicitud POST a `/api/auth/login` con las credenciales (nickname y contraseña).
2. El servidor valida las credenciales y, si son correctas, devuelve un token JWT.
3. El cliente debe incluir el token en el header de las peticiones protegidas.

### Endpoints protegidos
| Método | Endpoint                | Descripción                            |
| ------ |-------------------------|----------------------------------------|
| GET    | `/usuarios`             | Recuperar todos los usuarios (requiere token, pero el endpoint no usa el objeto Authentication)      |
| GET    | `/usuarios/me`          | Obtener perfil del usuario autenticado |
| PUT    | `/usuarios/{id}`        | Actualizar perfil                      |
| DELETE | `/usuarios/{id}`        | Eliminar usuario                       |
| DELETE | `/usuarios/{id}/cuenta` | Eliminar cuenta completa               |

La API valida que el usuario autenticado solo pueda modificar o eliminar su propio perfil.
Si el token es inválido o ha expirado, se devuelve un error `401 Unauthorized`.
Si el usuario intenta acceder a recursos de otros usuarios, se devuelve un error `403 Forbidden`.

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
* Para ejecutar los **tests unitarios** de las capas Controller y Service:

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
* **Servicios**: GET de todos los servicios, GET de servicios por ID Usuario, POST de un servicio, DELETE de un servicio por su ID y DELETE de un servicio por ID de Usuario al que pertenece.
* **Reviews**: GET de las reviews pertenecientes a un usuario y POST de una review.

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
* Logging configurado para diferenciar niveles (`INFO`, `WARN`, `ERROR`) según necesidades de desarrollo.

## Autora
Natalia Garré Ramo, alumna de 2º de DAM - Proyecto para el Trabajo de Fin de Grado · 2026








