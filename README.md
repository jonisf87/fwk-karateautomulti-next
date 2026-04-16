# FWK Karate AutoMulti

Repositorio de referencia para automatizacion E2E con Karate, microservicio Spring Boot, documentacion Antora y un arquetipo Maven reutilizable.

## Overview

El proyecto agrupa cuatro piezas:

* `code/`: microservicio demo Spring Boot con el endpoint `/products`
* `e2e/karate/`: suite E2E con Karate 2.0.2
* `code/archetype-karate-e2e/`: arquetipo Maven para generar proyectos alineados con este stack
* `code/docs/`: sitio Antora publicado en GitHub Pages

Documentacion online:

* https://jonisf87.github.io/fwk-karateautomulti/

## Requirements

* Java 21
* Maven 3.9 o superior
* Node.js 18 o superior
* Git

## Installation

```bash
git clone <repository-url>
cd fwk-karateautomulti
```

Validacion basica de dependencias:

```bash
cd code
mvn clean verify

cd ../e2e/karate
mvn clean verify '-Dkarate.options=--tags ~@local'
```

## Execution

Flujo local completo con el servicio demo:

```bash
cd code
mvn spring-boot:run
```

En otra terminal:

```bash
cd e2e/karate
mvn clean verify -Dkarate.env=local '-Dkarate.options=--tags @local'
```

Si necesitas otro puerto:

```bash
cd code
export APP_PORT=18081
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=$APP_PORT

cd ../e2e/karate
APP_PORT=18081 mvn clean verify -Dkarate.env=local '-Dkarate.options=--tags @local'
```

Documentacion local:

```bash
cd code/docs
npm ci
npm run build:dev
npm run preview
```

## Structure

```text
fwk-karateautomulti/
├── code/
│   ├── archetype-karate-e2e/
│   ├── docs/
│   └── src/
├── e2e/
│   └── karate/
└── README.md
```

## Contribution

1. Crea una rama desde `main`.
2. Manten el flujo remoto en `~@local` y usa `@local` solo cuando el servicio demo este levantado.
3. Si tocas documentacion, valida `code/docs` con la build Antora local.
4. Abre un Pull Request con el alcance bien acotado.
