# Technical Challenge

API REST reactiva con Spring Boot 3.2.0, WebFlux, R2DBC y H2 en memoria.

## Descripción

Este proyecto implementa un CRUD de productos con arquitectura reactiva usando:

- Java 17
- Spring Boot 3.2.0
- Spring WebFlux
- Spring Data R2DBC
- H2 Database (modo memoria)
- Bean Validation
- Reactor Mono/Flux
- JUnit 5 + Mockito
- JaCoCo

## Requisitos

- Java 17+
- Maven 3.9+
- Docker (opcional, para ejecución en contenedor)

## Estructura del proyecto

```text
src/
  main/
    java/
      com/enriquehs/
        controller/
        dto/
        error/
        mapper/
        model/
        repository/
        service/
        TechnicalChallengeApplication.java
    resources/
      application.yaml
      schema.sql
  test/
    java/
      com/enriquehs/
        controller/
        service/
```

## Configuración de R2DBC

La base de datos se configura en [src/main/resources/application.yaml](src/main/resources/application.yaml):

```yaml
spring:
  application:
    name: technical-challenge

  r2dbc:
    url: r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: ""

  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
```

Además, en [src/main/java/com/enriquehs/TechnicalChallengeApplication.java](src/main/java/com/enriquehs/TechnicalChallengeApplication.java) se inicializa el esquema con `schema.sql` para crear la tabla `PRODUCTOS` al arrancar la aplicación.

## Ejecutar la aplicación localmente

```bash
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación queda disponible en:

```text
http://localhost:8080
```

## Compilar y ejecutar con Docker

### 1. Crear el Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline

COPY src ./src

EXPOSE 8080

ENTRYPOINT ["./mvnw", "spring-boot:run"]
```

### 2. Construir la imagen

```bash
docker build -t technical-challenge .
```

### 3. Ejecutar el contenedor

```bash
docker run --name technical-challenge -p 8080:8080 -d technical-challenge
```

### 4. Verificar el servicio

```bash
curl http://localhost:8080/api/products
```

## Ejecutar tests y generar cobertura

```bash
./mvnw clean test jacoco:report
```

En Windows:

```powershell
.\mvnw.cmd clean test jacoco:report
```

El reporte HTML de JaCoCo se genera en:

```text
target/site/jacoco/index.html
```

## Endpoints disponibles

### GET /api/products
Obtiene todos los productos.

### GET /api/products/{id}
Obtiene un producto por su identificador.

### POST /api/products
Crea un nuevo producto.

Request ejemplo:

```json
{
  "name": "Teclado",
  "price": 40.00
}
```

### PUT /api/products/{id}
Actualiza un producto existente.

Request ejemplo:

```json
{
  "id": 1,
  "name": "Teclado",
  "price": 50.00,
  "createdAt": "2026-09-18T06:59:21.900767Z"
}
```

### DELETE /api/products/{id}
Elimina un producto por su id.

## Ejemplos de consumo con curl

### Obtener todos

```bash
curl -i http://localhost:8080/api/products
```

### Obtener por id

```bash
curl -i http://localhost:8080/api/products/1
```

### Crear producto

```bash
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Teclado",
    "price": 40.00
  }'
```

### Actualizar producto

```bash
curl -i -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "Teclado",
    "price": 50.00,
    "createdAt": "2026-09-18T06:59:21.900767Z"
  }'
```

### Eliminar producto

```bash
curl -i -X DELETE http://localhost:8080/api/products/1
```

### Validación de error

```bash
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Teclado",
    "price": -1
  }'
```

Esto devuelve un `400 Bad Request` por las validaciones del DTO.

## Colección Postman

Puede crearse una colección con estas peticiones:

1. GET `http://localhost:8080/api/products`
2. GET `http://localhost:8080/api/products/1`
3. POST `http://localhost:8080/api/products`
   ```json
   {
     "name": "Teclado",
     "price": 40.00
   }
   ```
4. PUT `http://localhost:8080/api/products/1`
   ```json
   {
     "id": 1,
     "name": "Teclado",
     "price": 50.00,
     "createdAt": "2026-09-18T06:59:21.900767Z"
   }
   ```
5. DELETE `http://localhost:8080/api/products/1`

## Manejo de errores

La API utiliza manejo centralizado de excepciones con `@RestControllerAdvice` para devolver respuestas JSON estandarizadas con:

- timestamp
- status
- error
- message
- path

## Cobertura de pruebas

La configuración JaCoCo está en [pom.xml](pom.xml). El objetivo es generar el informe de cobertura de forma automática durante la etapa `verify`.

## Autor

Enrique Huanca Solís
