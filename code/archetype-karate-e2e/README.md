# Archetype Karate E2E

Arquetipo Maven para generar proyectos con `e2e/karate` y, opcionalmente, con un modulo `code` Spring Boot.

## Overview

El proyecto generado hereda el stack actual del repositorio:

* Java 21
* Karate 2.0.2
* runner JUnit Platform
* soporte `APP_PORT` para `@local`
* workflow de CI base

## Requirements

* Java 21
* Maven 3.9 o superior

## Installation

Desde la raiz del repositorio:

```bash
mvn -f code/archetype-karate-e2e/pom.xml clean install
```

## Execution

Generar proyecto solo con E2E:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.example.archetypes \
  -DarchetypeArtifactId=archetype-karate-e2e \
  -DarchetypeVersion=1.0.6-SNAPSHOT \
  -DgroupId=com.acme \
  -DartifactId=demo-e2e-no-micro \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackage=com.acme.demo \
  -DincludeCodeModule=false \
  -DinteractiveMode=false
```

Generar proyecto con microservicio demo:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.example.archetypes \
  -DarchetypeArtifactId=archetype-karate-e2e \
  -DarchetypeVersion=1.0.6-SNAPSHOT \
  -DgroupId=com.acme \
  -DartifactId=demo-e2e-con-micro \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackage=com.acme.demo \
  -DincludeCodeModule=true \
  -DinteractiveMode=false
```

## Structure

```text
demo-e2e-con-micro/
├── pom.xml
├── code/
└── e2e/karate/
```

## Contribution

* Cualquier cambio de versiones o flujo local del repo principal debe replicarse aqui.
* Valida siempre el proyecto generado, no solo el modulo del arquetipo.
