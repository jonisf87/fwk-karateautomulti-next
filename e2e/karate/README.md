# Módulo E2E Karate

Este módulo contiene las pruebas end-to-end escritas con Karate.

## Ejecutar pruebas

Ejecutar todas las pruebas:

```bash
mvn -q -f e2e/karate/pom.xml test
```

Filtrar por tags y/o entorno:

```bash
mvn -q -f e2e/karate/pom.xml test "-Dkarate.options=--tags @tuTag" "-Dkarate.env=local"
```

## Estructura

- Features: `src/test/resources/tests`
- Configuración: `src/test/resources/karate-config.js`, `config-*.yml`
- Runner JUnit5: `src/test/java/tests/KarateRunnerTest.java`

## Ejecutar contra microservicio local (opcional)

1) Arranca el micro en otra terminal (ver `code/README.md`).
2) Ejecuta las pruebas con entorno `local`:

```bash
mvn -q -f e2e/karate/pom.xml test -Dkarate.env=local -D"karate.options=--tags @local"
```

## Reportes

- HTML: `target/karate-reports/karate-summary.html`
- Timeline: `target/karate-reports/karate-timeline.html`
- Cucumber JSON: `target/cucumber-report.json`
- JUnit XML: `target/surefire-reports/`

Para combinar reportes JSON: `node combine-reports.js` (si aplica en tu flujo).
