# FWK Karate AutoMulti

Repositorio de referencia para **shift-left**, **branching strategy**, **Conventional Commits**, **mutation testing** y **judge-assisted triage** sobre una base Spring Boot + Karate.

## Overview

El alcance activo del repositorio es este:

- `code/`: microservicio demo Spring Boot con el endpoint `/products`
- `e2e/karate/`: suite E2E con Karate
- `.github/workflows/`: CI de quality gates, policy y judge
- `.githooks/` y `scripts/`: automatizacion local y operativa

## Requirements

- Java 21
- Maven 3.9 o superior
- Node.js 18 o superior
- Git

## Installation

```bash
git clone <repository-url>
cd fwk-karateautomulti
git config core.hooksPath .githooks
```

Validacion base del stack:

```bash
mvn -B -ntp -f code/pom.xml clean verify
mvn -B -ntp -f code/pom.xml test-compile org.pitest:pitest-maven:mutationCoverage
mvn -B -ntp -f e2e/karate/pom.xml -DskipTests clean verify
```

## Local execution

Arranca primero el servicio demo:

```bash
mvn -B -ntp -f code/pom.xml spring-boot:run -Dspring-boot.run.arguments=--server.port=18081
```

En otra terminal, ejecuta Karate en modo local:

```bash
APP_PORT=18081 mvn -B -ntp -f e2e/karate/pom.xml clean test -Dkarate.env=local '-Dkarate.options=--tags @local'
```

Si necesitas otro puerto:

```bash
export APP_PORT=18081
mvn -B -ntp -f code/pom.xml spring-boot:run -Dspring-boot.run.arguments=--server.port=$APP_PORT
APP_PORT=$APP_PORT mvn -B -ntp -f e2e/karate/pom.xml clean test -Dkarate.env=local '-Dkarate.options=--tags @local'
```

## Structure

```text
fwk-karateautomulti/
├── .githooks/
├── .github/workflows/
├── code/
│   ├── pom.xml
│   └── src/
├── e2e/karate/
├── scripts/
└── README.md
```

## Shift-left phase

La fase 1 del repositorio deja automatizados estos gates:

1. **`Shift Left Phase`**
   - `quality-gate`: compila y ejecuta `verify` del microservicio con Java 21, y valida el modulo Karate sin ejecutar escenarios locales.
   - `mutation-testing`: ejecuta PIT sobre `code/` y publica `code/target/pit-reports`.
   - `karate-e2e`: levanta el servicio demo en `APP_PORT=18081`, ejecuta Karate con `@local` y publica artifacts de diagnostico.
2. **`Contribution Policy`**
   - `conventional-commits`: valida todos los commits del push o PR.
   - `branch-name-policy`: valida en PR el convenio de ramas.
3. **`karate-failure-triage`**
   - consume los artifacts del job `karate-e2e` cuando Karate falla;
   - construye un payload estructurado con logs, reports y metadata del run;
   - llama al juez Gemini solo para clasificar la causa del fallo;
   - publica annotation, resumen y artifact de triage.

Los checks required actuales en `main` son:

- `quality-gate`
- `mutation-testing`
- `karate-e2e`
- `conventional-commits`
- `branch-name-policy`
- `karate-failure-triage`

La proteccion remota de `main` se aplica con:

```bash
scripts/apply-branch-protection.sh jonisf87 fwk-karateautomulti-next main
```

La proteccion deja `main` con:

- pull request obligatorio;
- 1 approval minimo;
- dismiss stale reviews;
- conversation resolution;
- linear history;
- sin force-push;
- sin branch deletion.

## Judge-assisted triage phase

La fase 2 introduce un juez LLM orientado **solo** a Karate. No evalua respuestas de IA genericas ni compara respuestas de otros modelos. Su salida es operativa y estructurada:

- `category`
- `confidence`
- `summary`
- `evidence`
- `reasoning`
- `recommended_action`
- `annotation_level`

No se usa scoring numerico porque aqui interesa clasificar causa y accion siguiente, no puntuar calidad.

### Configuracion del juez

Secret obligatorio en GitHub Actions:

- `KARATE_JUDGE_API_KEY`

Configuracion no sensible recomendada como repository/environment variables:

- `KARATE_JUDGE_MODEL=gemini-2.5-flash-lite`
- `KARATE_JUDGE_TIMEOUT_MS=30000`

La implementacion actual usa:

- `scripts/judge/karate-failure-triage-prompt.md`
- `scripts/judge/karate-failure-triage-schema.json`
- `scripts/judge/build-karate-triage-input.mjs`
- `scripts/judge/karate-failure-triage.mjs`

### Categorias del juez

- `ENVIRONMENT_UNAVAILABLE`
- `DEMO_SERVICE_FAILURE`
- `FLAKY_TEST`
- `TEST_DEFECT`
- `UNKNOWN`

### Ramas demo para provocar escenarios

El workflow `Shift Left Phase` activa modos de demostracion segun el nombre de rama:

- `test/judge-environment-unavailable`
- `test/judge-demo-service-failure`
- `test/judge-flaky-test`

Comportamiento esperado:

1. `test/judge-environment-unavailable`: ejecuta una feature que apunta a un host invalido y genera una senal clara de indisponibilidad de entorno.
2. `test/judge-demo-service-failure`: reserva `APP_PORT` antes de arrancar Spring Boot para provocar fallo de arranque del servicio demo.
3. `test/judge-flaky-test`: ejecuta una feature que falla en la primera pasada y pasa en el rerun para dejar evidencia explicita de flakiness.

### Artifacts del juez

Cuando Karate falla, el pipeline publica:

- `karate-diagnostics`
  - logs del demo service
  - respuesta del health-check
  - `surefire-reports`
  - reports HTML/JSON/XML de Karate
  - `judge-run-metadata.json`
- `karate-failure-triage`
  - `karate-triage-input.json`
  - `karate-triage-result.json`

## Contribution

1. Trabaja siempre desde una rama creada desde `main`; el convenio recomendado es `type/TICKET-short-description` o `type/short-description` (`feature/QA-123-shift-left-phase`, `fix/karate-local-port`).
2. Instala los hooks versionados del repo con `git config core.hooksPath .githooks`.
3. Usa Conventional Commits estrictos: `feat(ci): add shift-left workflow`, `test(code): cover products endpoint`.
4. El hook `pre-commit` valida naming de rama y ejecuta Maven sobre los modulos tocados; si hay cambios en `code/` corre `mvn -B -ntp -f code/pom.xml verify` y si hay cambios en `e2e/karate/` corre `mvn -B -ntp -f e2e/karate/pom.xml -DskipTests verify`.
5. El hook `commit-msg` reutiliza `scripts/validate-conventional-commit.sh`; el workflow remoto reutiliza la misma validacion para no duplicar reglas.
6. Manten el flujo remoto en `~@local` y usa `@local` solo cuando el servicio demo este levantado. Para evitar colisiones locales, el flujo recomendado usa `APP_PORT=18081`.
7. `main` es solo rama protegida de integracion: trabaja en `feature/*`, `fix/*` o `test/*`, y fusiona por PR usando `squash` o `rebase`.
8. Las ramas `test/judge-*` existen para demostrar clasificacion de fallos y no deben mergearse a `main`.
9. Si necesitas reaplicar la politica remota de merge y la proteccion de `main`, usa `scripts/apply-branch-protection.sh`.
10. Si vas a usar el juez en CI, crea antes el secret `KARATE_JUDGE_API_KEY` y, si quieres sobrescribir el modelo por defecto, define `KARATE_JUDGE_MODEL`.
11. Documenta los cambios en los README activos del repo cuando afecten al flujo local, CI o politica de contribucion.
12. Abre un Pull Request con el alcance bien acotado.
