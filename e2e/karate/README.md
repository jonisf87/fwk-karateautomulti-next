# Karate E2E

Suite E2E del repositorio, basada en Karate 2.0.2 y Java 21.

## Overview

La suite separa dos modos de ejecucion:

* `~@local`: validacion estandar del repositorio, sin depender del microservicio local
* `@local`: flujo real del desarrollador contra el servicio demo levantado manualmente

## Requirements

* Java 21
* Maven 3.9 o superior

## Installation

```bash
cd e2e/karate
mvn clean verify '-Dkarate.options=--tags ~@local'
```

## Execution

Flujo remoto estandar:

```bash
mvn clean verify '-Dkarate.options=--tags ~@local'
```

Flujo local:

```bash
mvn clean verify -Dkarate.env=local '-Dkarate.options=--tags @local'
```

Flujo local con puerto personalizado:

```bash
APP_PORT=18081 mvn clean verify -Dkarate.env=local '-Dkarate.options=--tags @local'
```

Filtrado por tags:

```bash
mvn test '-Dkarate.options=--tags @smoke'
mvn test '-Dkarate.options=--tags @local'
```

## Structure

```text
e2e/karate/
├── pom.xml
├── combine-reports.js
└── src/test/
    ├── java/tests/
    └── resources/
        ├── karate-config.js
        ├── config-local.yml
        ├── config-pre.yml
        └── tests/products/
```

## Contribution

* Usa `~@local` para la validacion estandar.
* Usa `@local` solo con el microservicio demo levantado.
* Mantén features, datos de prueba y configuracion por entorno alineados.
