# Microservicio demo

Modulo Spring Boot 3.5.13 sobre Java 21. Su objetivo es dar soporte al flujo local de la suite Karate y a los quality gates del pipeline.

## Overview

El servicio expone un unico endpoint:

* `GET /products`

Respuesta esperada:

```json
[
  { "id": 1, "name": "Product A", "price": 10.0 },
  { "id": 2, "name": "Product B", "price": 20.0 }
]
```

## Requirements

* Java 21
* Maven 3.9 o superior

## Installation

```bash
cd code
mvn -B -ntp clean verify
```

## Execution

Arranque normal:

```bash
mvn -B -ntp spring-boot:run
```

Puerto personalizado:

```bash
export APP_PORT=18081
mvn -B -ntp spring-boot:run -Dspring-boot.run.arguments=--server.port=$APP_PORT
```

Comprobacion rapida:

```bash
curl http://localhost:${APP_PORT:-8080}/products
```

## Structure

```text
code/
├── pom.xml
└── src/
    ├── main/java/com/example/demo/
    ├── main/resources/
    └── test/java/com/example/demo/
```

## Contribution

- Mantener estable el endpoint `/products`.
- Cualquier cambio que afecte al flujo local debe validarse tambien desde `e2e/karate`.
- Mantener cobertura y mutacion a nivel de CI; este modulo participa en `quality-gate` y `mutation-testing`.
