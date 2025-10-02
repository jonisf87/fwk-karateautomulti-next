# Archetype Karate E2E

Arquetipo Maven para generar:
# Archetype Karate E2E

Arquetipo Maven para generar:
- Módulo siempre: `e2e/karate` con tests Karate.
- Módulo opcional: `code` (micro Spring Boot) controlado por `includeCodeModule`.

## 1. Instalar el arquetipo localmente

Desde la raíz del repo que contiene `code/archetype-karate-e2e`:

```bash
mvn -f code/archetype-karate-e2e clean install
```

## 2. Generar proyecto SIN micro (modo offline por defecto)

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
  -B

cd demo-e2e-no-micro
mvn -pl e2e/karate test
```

El proyecto generado incluye un feature OFFLINE (sin llamadas HTTP) para garantizar que los tests pasan incluso en entornos restringidos.

Ejecutar:
```bash
mvn -pl e2e/karate test
```

### (Opcional) Activar mock embebido
Dentro del feature `getProductsLocal.feature` hay un escenario comentado `(ejemplo mock embebido)`. Descoméntalo si tu entorno soporta `karate.start()`:
```gherkin
Scenario: (ejemplo mock embebido)
  * def mock = karate.start('classpath:mocks/products-mock.feature')
  Given url mock.url + 'products'
  When method get
  Then status 200
```
Si obtienes `Connection refused`, deja el escenario comentado y permanece en modo offline.

## 3. Generar proyecto CON micro

```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.example.archetypes \
  -DarchetypeArtifactId=archetype-karate-e2e \
  -DarchetypeVersion=1.0.6-SNAPSHOT \
  -DgroupId=com.acme \
  -DartifactId=demo-e2e-with-micro \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackage=com.acme.demo \
  -DincludeCodeModule=true \
  -B
```

Levantar micro (puerto 9090 por ejemplo):

```bash
cd demo-e2e-with-micro
export APP_PORT=9090
mvn -pl code -am spring-boot:run -Dspring-boot.run.arguments=--server.port=$APP_PORT
```

En otra terminal:

```bash
cd demo-e2e-with-micro
export APP_PORT=9090
mvn -pl e2e/karate test
```

En Windows PowerShell:

```powershell
$env:APP_PORT=9090
mvn -pl e2e/karate test
```

## 4. Notas

- Variable `includeCodeModule=true|false`.
- `karate-config.js` selecciona `demoApplicationUrl` según `APP_PORT` o entorno.
- `getProductsLocal.feature` ahora está en modo offline estable y documenta cómo habilitar un mock embebido opcional.
