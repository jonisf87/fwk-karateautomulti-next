# Karate E2E

Suite E2E del repositorio, basada en Karate 2.0.2 y Java 21. Es la base tanto del flujo local como del pipeline de triage asistido por juez.

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
mvn -B -ntp clean verify '-Dkarate.options=--tags ~@local'
```

## Execution

Flujo remoto estandar:

```bash
mvn -B -ntp clean verify '-Dkarate.options=--tags ~@local'
```

Flujo local:

```bash
mvn -B -ntp clean verify -Dkarate.env=local '-Dkarate.options=--tags @local'
```

Flujo local con puerto personalizado:

```bash
APP_PORT=18081 mvn -B -ntp clean verify -Dkarate.env=local '-Dkarate.options=--tags @local'
```

Filtrado por tags:

```bash
mvn -B -ntp test '-Dkarate.options=--tags @smoke'
mvn -B -ntp test '-Dkarate.options=--tags @local'
```

## Judge demos

La fase de triage usa ramas demo para provocar fallos clasificables:

- `test/judge-environment-unavailable`
- `test/judge-demo-service-failure`
- `test/judge-flaky-test`

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

- Usa `~@local` para la validacion estandar.
- Usa `@local` solo con el microservicio demo levantado.
- Mantén features, datos de prueba y configuracion por entorno alineados.
- Si un fallo debe ser triageable por el juez, deja evidencia suficiente en reports, logs y nombre del escenario.
