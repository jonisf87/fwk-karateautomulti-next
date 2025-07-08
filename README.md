# 🚀 Proyecto de Automatización Karate + Gatling - Izertis

Este proyecto combina **Karate** para pruebas funcionales, proporcionando un framework robusto para pruebas automatizadas.

---

## 📋 Dependencias Necesarias

Para ejecutar este proyecto, asegúrate de tener instaladas las siguientes herramientas y dependencias:

- ☕ **Java 17** o superior: Lenguaje base del proyecto.
- 📦 **Maven 3.6** o superior: Para la gestión de dependencias y ejecución de pruebas.
- 🌐 **Node.js**: Necesario para ejecutar el script `combine-reports.js`.
- 🔧 **Git**: Para clonar y gestionar el repositorio.

### Verificar Instalaciones

```bash
java -version    # Debe mostrar Java 17+
mvn -version     # Debe mostrar Maven 3.6+
node -v          # Debe mostrar Node.js instalado
git --version    # Para clonar y cambiar ramas
```

---

## 🚀 Cómo Ejecutar el Proyecto

### 1. Clonar el Repositorio

```bash
git clone <repository-url>
cd karate
```

### 2. Instalar Dependencias

```bash
mvn clean install
```

### 3. Ejecutar Pruebas

#### Pruebas Funcionales con Karate

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

## 📁 Estructura del Proyecto

El proyecto está organizado de la siguiente manera:

```
karate/
├── src/
│   ├── test/
│   │   └── java/
│   │     ├── tests/                 # Pruebas funcionales y de rendimiento
│   │     │   ├── KarateRunnerTest.java # Clase principal para ejecutar pruebas Karate
│   │     │   └──products/           # Pruebas relacionadas con api products
│   │     ├── karate-config.js       # Configuración global de Karate
│   │     ├── log4j2.properties      # Configuración de logging
│   │     ├── logback-test.xml       # Configuración de logging para pruebas
│   │     └── config-pre.yml         # Configuración específica para el entorno "pre"
├── target/                             # Archivos generados
├── .mvn/                               # Configuración del wrapper de Maven
│   └── wrapper/
│       ├── MavenWrapperDownloader.java # Descargador del wrapper de Maven
│       └── maven-wrapper.properties    # Configuración del wrapper
├── combine-reports.js                  # Script para combinar reportes JSON de Karate
├── pom.xml                             # Configuración Maven
└── README.md                           # Documentación del proyecto
```

### Descripción de Archivos y Carpetas

- **`src/test/java/tests/`**: Contiene las pruebas funcionales y de rendimiento.
  - **`KarateRunnerTest.java`**: Clase principal para ejecutar las pruebas Karate en paralelo y generar reportes.
  - **`products/`**: Carpeta con pruebas relacionadas con api products.
- **`src/test/java/karate-config.js`**: Archivo de configuración global de Karate para gestionar entornos y variables.
- **`src/test/java/log4j2.properties`**: Configuración de logging para las pruebas.
- **`src/test/java/logback-test.xml`**: Configuración de logging para pruebas.
- **`src/test/java/config-pre.yml`**: Archivo de configuración específico para el entorno "pre".
- **`target/`**: Carpeta generada automáticamente que contiene resultados de pruebas, reportes y otros archivos temporales.
- **`.mvn/wrapper/`**: Configuración del wrapper de Maven para garantizar la compatibilidad de versiones.
- **`combine-reports.js`**: Script Node.js que combina los reportes JSON generados por Karate en un único archivo, haciéndolo compatible con herramientas como Jira.
- **`pom.xml`**: Archivo de configuración Maven que define las dependencias y plugins necesarios.
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