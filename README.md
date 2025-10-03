# 🚀 Proyecto de Automatización Karate + Gatling - Izertis

Este proyecto utiliza **Karate** como framework principal para pruebas funcionales y de integración, permitiendo la automatización robusta de pruebas E2E sobre servicios REST y microservicios.

---

## 🌐 Documentación Online

Consulta el sitio generado con Antora en GitHub Pages para ver guías, referencias y ejemplos actualizados:

- 👉 [https://jonisf87.github.io/fwk-karateautomulti/](https://jonisf87.github.io/fwk-karateautomulti/)

---

## 🚀 Cómo Ejecutar el Proyecto

### 0. Prerrequisitos

Asegúrate de tener instaladas las siguientes herramientas globales:

- ☕ **Java 17** o superior
- 📦 **Maven 3.6** o superior
- 🌐 **Node.js**
- 🔧 **Git**

Puedes verificar las instalaciones con:
```bash
java -version    # Debe mostrar Java 17+
mvn -version     # Debe mostrar Maven 3.6+
node -v          # Debe mostrar Node.js instalado
git --version    # Para clonar y cambiar ramas
```

### 1. Clonar el Repositorio

```bash
git clone <repository-url>
cd auto-karate-fw
```

### 2. Instalar dependencias

#### Microservicio (carpeta `code`)
```bash
cd code
mvn clean install
```
Esto descargará todas las dependencias necesarias para el microservicio y generará el JAR ejecutable.

#### Karate (carpeta `e2e/karate`)
```bash
cd e2e/karate
mvn clean install
```
Esto descargará todas las dependencias necesarias para ejecutar los tests E2E con Karate.

### 3. Inicializar el microservicio local (opcional para pruebas locales)

```bash
cd code
mvn clean install
java -jar target/karate-demo-micro-0.0.1-SNAPSHOT.jar
# El microservicio quedará disponible en http://localhost:8080/products
```

### 4. Ejecutar Pruebas

#### Comandos básicos para ejecutar pruebas Karate

```bash
# Comando básico para ejecutar pruebas Karate
mvn clean test

# Agregar tags específicos
mvn clean test "-Dkarate.options=--tags @yourTag"

# Configurar el entorno (por defecto se usa el configurado en karate-config.js)
mvn clean test "-Dkarate.env=yourEnv"

# Tag y entorno
mvn clean test "-Dkarate.options=--tags @yourTag" "-Dkarate.env=yourEnv"
```

#### Ejecución local (microservicio propio)

1. Inicializa el microservicio demo:
   ```bash
   cd code
   mvn clean install
   java -jar target/karate-demo-micro-0.0.1-SNAPSHOT.jar
   # El microservicio quedará disponible en http://localhost:8080/products
   ```

2. En otra terminal, ejecuta las pruebas locales:
   ```bash
   cd e2e/karate
   mvn clean install
   mvn clean test -Dkarate.env=local -D"karate.options=--tags @local"
   ```

---

## 📁 Estructura del Proyecto

El proyecto está organizado de la siguiente manera:

```
auto-karate-fw/
├── code/archetype-karate-e2e/     # Arquetipo Maven para generar nuevos proyectos Karate E2E
├── code/                        # Microservicio Java Spring Boot
│   ├── src/
│   └── pom.xml
├── e2e/
│   └── karate/
│       ├── src/
│       │   └── test/
│       │       ├── java/                       # Código Java de tests y utilidades
│       │       │   └── tests/                  # (Ej: KarateRunnerTest.java, utils, etc.)
│       │       └── resources/                  # Features y configuración Karate
│       │           ├── karate-config.js        # Configuración global Karate
│       │           ├── config-local.yml        # Configuración local
│       │           ├── config-pre.yml          # Configuración pre
│       │           ├── log4j2.properties       # Logging
│       │           ├── logback-test.xml        # Logging para pruebas
│       │           └── tests/                  # Features Karate organizados por dominio
│       │               └── products/
│       │                   ├── getProductsLocal/   # Prueba local
│       │                   ├── getAllProducts/     # Prueba remota
│       │                   └── ...
│       ├── combine-reports.js                  # Script para combinar reportes
│       ├── pom.xml                             # Configuración Maven para Karate
│       └── .gitignore
├── code/docs/                    # Sitio Antora (UI local, overrides y packaging)
│   ├── antora-playbook.dev.yml   # Dev/offline (usa carpeta UI extraída)
│   ├── antora-playbook.yml       # Prod (usa ui/ui-bundle.zip)
│   ├── ui/ui-bundle-extract/     # UI extraída + overrides aplicados
│   ├── ui/ui-bundle.zip          # Bundle UI para builds deterministas (generado)
│   └── scripts/bundle-ui.js      # Empaquetado UI con Node (sin zip/bash)
└── README.md
```

### Descripción de Archivos y Carpetas

- **`code/`**: Microservicio Java Spring Boot para pruebas locales.
- **`code/archetype-karate-e2e/`**: Arquetipo Maven que permite generar proyectos de automatización Karate con (o sin) un módulo opcional de microservicio Spring Boot. Útil para estandarizar la creación rápida de nuevos repos.
- **`e2e/karate/`**: Proyecto de automatización Karate.
  - **`src/test/java/`**: Código Java de tests y utilidades.
    - **`tests/`**: Clases de test (por ejemplo, `KarateRunnerTest.java`).
    - **`utils/`**: Clases utilitarias Java para soporte de tests Karate.
  - **`src/test/resources/`**: Configuración, logs y features Karate.
    - **`karate-config.js`**: Configuración global de Karate.
    - **`config-local.yml`**: Configuración para entorno local.
    - **`config-pre.yml`**: Configuración para entorno pre.
    - **`log4j2.properties`** y **`logback-test.xml`**: Logging.
    - **`tests/`**: Features Karate organizados por dominio.
      - **`products/getProductsLocal/`**: Pruebas locales contra el microservicio demo.
      - **`products/getAllProducts/`**: Pruebas remotas contra el entorno pre/productivo.
      - ...otras carpetas de pruebas...
  - **`combine-reports.js`**: Script para combinar reportes JSON.
  - **`pom.xml`**: Configuración Maven para Karate.
  - **`.gitignore`**: Exclusiones de control de versiones.
- **`README.md`**: Documentación del proyecto.

---


## 📚 Documentación (Antora)

Construcción del sitio de documentación:

```bash
# Dev/offline (UI extraída, iteración rápida)
cd code/docs
npm install
npm run build:dev
npm run preview  # http://localhost:5080

# Prod (determinista con zip UI)
npm run ui:bundle
npm run build:prod
```

Personalizaciones de UI aplicadas:
- Header: sin Products/Services ni buscador.
- Footer: contenido personalizado.
- Panel "Explore" eliminado.

---

## 🧬 Arquetipo Maven: `archetype-karate-e2e`

El repositorio incluye un arquetipo localizado en `code/archetype-karate-e2e` que permite crear un nuevo proyecto Karate listo para usar, con opción de incluir un microservicio demo.

### 1. Instalar/Actualizar el arquetipo localmente

Desde la carpeta raíz del repo:

```bash
mvn -q -f code/archetype-karate-e2e/pom.xml clean install
```

Esto instalará el arquetipo en tu repositorio Maven local (`~/.m2`).

### 2. Generar un nuevo proyecto SIN micro (modo offline por defecto)

```bash
mvn archetype:generate \
   -DarchetypeGroupId=com.example.archetypes \
   -DarchetypeArtifactId=archetype-karate-e2e \
   -DarchetypeVersion=1.0.6-SNAPSHOT \
   -DgroupId=com.acme.demo \
   -DartifactId=demo-e2e-no-micro \
   -Dversion=1.0.0-SNAPSHOT \
   -DincludeCodeModule=false \
   -DinteractiveMode=false
```

Resultado: proyecto multi-módulo sólo con `e2e/karate` y un feature offline (sin HTTP) que pasa siempre.

### 3. Generar un proyecto CON micro

```bash
mvn archetype:generate \
   -DarchetypeGroupId=com.example.archetypes \
   -DarchetypeArtifactId=archetype-karate-e2e \
   -DarchetypeVersion=1.0.6-SNAPSHOT \
   -DgroupId=com.acme.demo \
   -DartifactId=demo-e2e-con-micro \
   -Dversion=1.0.0-SNAPSHOT \
   -DincludeCodeModule=true \
   -DinteractiveMode=false
```

Resultado: proyecto multi-módulo con:
* `code/` Spring Boot (endpoint `/products`)
* `e2e/karate/` feature que hace GET real contra el micro.

### 4. Variables disponibles

| Propiedad              | Descripción                                                  | Ejemplo |
|------------------------|--------------------------------------------------------------|---------|
| `includeCodeModule`    | `true` para incluir microservicio, `false` para sólo tests   | true    |
| `karateVersion`        | Versión de Karate usada en el módulo de pruebas              | 1.4.1   |
| `groupId` / `artifactId` | Coordenadas Maven del nuevo proyecto                      |         |

Si algún placeholder no se filtra (corte de red o mirrors), un script post-generación aplica reemplazos de respaldo.

### 5. Flujo típico tras generar

```bash
cd demo-e2e-con-micro
mvn clean install -DskipTests
mvn -pl code spring-boot:run &   # arranca micro (o usar java -jar code/target/*.jar)
mvn -pl e2e/karate test          # ejecuta tests Karate
```

### 6. Estructura generada (con micro)

```
demo-e2e-con-micro/
├── pom.xml (parent)
├── code/ (microservicio Spring Boot)
└── e2e/karate/ (tests Karate)
```

### 7. Notas
* El feature por defecto en modo con micro valida lista de productos.
* En modo sin micro se usa un feature offline para evitar dependencias de red / puertos.
* Puedes añadir mocks embebidos activando el ejemplo comentado en el feature offline.

---

## 📊 Reportes y Resultados

El proyecto genera los siguientes reportes y resultados:

### Reportes de Karate

Tras la ejecución de las pruebas, se generan varios reportes en la carpeta `target`:

1. **Reporte HTML**:
   - Ubicación: `target/karate-reports/`
   - Archivo principal: `karate-summary.html`
   - Descripción: Este reporte proporciona un resumen visual de las pruebas ejecutadas, incluyendo el número de escenarios pasados y fallidos, así como enlaces a los detalles de cada prueba.

2. **Reporte de Línea de Tiempo**:
   - Ubicación: `target/karate-reports/`
   - Archivo: `karate-timeline.html`
   - Descripción: Muestra una línea de tiempo interactiva con la duración de cada escenario, útil para identificar cuellos de botella en las pruebas.

3. **Reporte de Características (Features)**:
   - Ubicación: `target/karate-reports/`
   - Archivos: `karate-summary.html` y `karate-tags.html`
   - Descripción: Incluye detalles de las características probadas, agrupadas por etiquetas (tags) y escenarios.

4. **Reporte Cucumber JSON**:
   - Ubicación: `target/`
   - Archivo: `cucumber-report.json`
   - Descripción: Archivo en formato JSON compatible con herramientas externas para análisis adicional o integración con sistemas como Jenkins.

5. **Reporte JUnit XML**:
   - Ubicación: `target/surefire-reports/`
   - Archivo: `TEST-tests.KarateRunnerTest.xml`
   - Descripción: Reporte en formato XML compatible con JUnit, útil para integraciones con sistemas de CI/CD.

6. **Reportes HTML Detallados por Escenario**:
   - Ubicación: `target/karate-reports/`
   - Ejemplo: `tests.products.e2e.searchById.searchById.html`
   - Descripción: Reportes individuales para cada escenario, con detalles de los pasos ejecutados, tiempos y resultados.

7. **Reporte de Tags**:
   - Ubicación: `target/cucumber-html-reports/`
   - Archivo: `overview-tags.html`
   - Descripción: Muestra estadísticas agrupadas por etiquetas, incluyendo el número de pasos y escenarios pasados o fallidos.

8. **Reporte de Fallos**:
   - Ubicación: `target/cucumber-html-reports/`
   - Archivo: `overview-failures.html`
   - Descripción: Lista los escenarios fallidos (si los hay) para facilitar su análisis.

### Uso del Script `combine-reports.js`

El archivo `combine-reports.js` es un script Node.js que combina múltiples reportes JSON generados por Karate en un único archivo. Esto es útil para integraciones con herramientas como Jira, donde se requiere un único reporte consolidado.

Para ejecutarlo:

```bash
node combine-reports.js
```

El script buscará los reportes JSON en la carpeta de destino y generará un archivo combinado llamado `cucumber-result.json` en la carpeta `target`.

---

## 🤝 Contribuir

1. Fork el proyecto.
2. Crea una feature branch (`git checkout -b feature/nueva-funcionalidad`).
3. Commit tus cambios (`git commit -am 'Agregar nueva funcionalidad'`).
4. Push a la rama (`git push origin feature/nueva-funcionalidad`).
5. Crear un Pull Request.

---

## 🎯 Próximos Pasos

1. **Configura tu entorno**: Asegúrate de tener todas las dependencias instaladas.
2. **Elige tu enfoque**: Decide si necesitas pruebas funcionales, de rendimiento o ambas.
3. **Ejecuta las pruebas**: Usa los comandos proporcionados para comenzar.
4. **Explora los reportes**: Revisa los resultados en los reportes nativos de Karate o Gatling.
5. **Integra con Jira**: Usa `combine-reports.js` para consolidar reportes.

---

¿Tienes dudas? Revisa la estructura del proyecto o consulta la documentación de Karate para más detalles.