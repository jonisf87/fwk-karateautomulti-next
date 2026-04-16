# Archetype Karate E2E

Arquetipo Maven para generar proyectos con `e2e/karate` y, opcionalmente, con un modulo `code` Spring Boot.

## Overview

El proyecto generado hereda el stack actual del repositorio:

* Java 21
* Karate 2.0.2
* runner JUnit Platform
* soporte `APP_PORT` para `@local`
* workflow de CI base

Coordenadas del arquetipo:

* `io.github.jonisf87.archetypes:archetype-karate-e2e`

## Requirements

* Java 21
* Maven 3.9 o superior

## Installation

Uso local desde la raiz del repositorio:

```bash
mvn -f code/archetype-karate-e2e/pom.xml clean install
```

Uso remoto desde GitHub Packages:

1. Crea un token classic con `read:packages`.
2. Añadelo a `~/.m2/settings.xml`:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <activeProfiles>
    <activeProfile>github-archetype</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github-archetype</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo.maven.apache.org/maven2</url>
        </repository>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/jonisf87/fwk-karateautomulti</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>jonisf87</username>
      <password>${env.GITHUB_PACKAGES_TOKEN}</password>
    </server>
  </servers>
</settings>
```

Exporta el token antes de usar Maven:

```bash
export GITHUB_PACKAGES_TOKEN=<tu-token-read-packages>
```

## Execution

Generar proyecto solo con E2E en local:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.github.jonisf87.archetypes \
  -DarchetypeArtifactId=archetype-karate-e2e \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.acme \
  -DartifactId=demo-e2e-no-micro \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackage=com.acme.demo \
  -DincludeCodeModule=false \
  -DinteractiveMode=false
```

Generar proyecto con microservicio demo desde GitHub Packages:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.github.jonisf87.archetypes \
  -DarchetypeArtifactId=archetype-karate-e2e \
  -DarchetypeVersion=1.0.0 \
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
* Las releases publicas se publican con tags `vX.Y.Z` y el workflow `.github/workflows/publish-archetype.yml`.
