# 🚀 Proyecto de Automatización Karate + Gatling - Izertis

Este proyecto utiliza **Karate** como framework principal para pruebas funcionales y de integración, permitiendo la automatización robusta de pruebas E2E sobre servicios REST y microservicios.

---


## � Cómo Ejecutar el Proyecto

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

---

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

## 📁 Estructura del Proyecto

El proyecto está organizado de la siguiente manera:

```
auto-karate-fw/
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
└── README.md
```


### Descripción de Archivos y Carpetas

- **`code/`**: Microservicio Java Spring Boot para pruebas locales.
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