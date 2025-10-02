# Módulo Microservicio (code)

Módulo opcional con un microservicio Spring Boot 3.x de ejemplo.
Expone un endpoint REST para productos, utilizado por los tests E2E (Karate).

## Endpoints

- `GET /products` → Lista estática de productos de ejemplo:
	```json
	[
		{ "id": 1, "name": "Product A", "price": 10.0 },
		{ "id": 2, "name": "Product B", "price": 20.0 }
	]
	```

## Configuración y ejecución

- Puerto por defecto: `8080` (ver `code/src/main/resources/application.properties`).
- Puedes cambiar el puerto con `APP_PORT` o `-Dspring-boot.run.arguments=--server.port=XXXX`.

Arranque local (desde la raíz del repo):

```bash
mvn -q -f code/pom.xml spring-boot:run
```

Arranque en puerto personalizado (Linux/macOS/WSL):

```bash
export APP_PORT=9090
mvn -q -f code/pom.xml spring-boot:run -Dspring-boot.run.arguments=--server.port=$APP_PORT
```

Arranque en puerto personalizado (Windows PowerShell):

```powershell
$env:APP_PORT=9090
mvn -q -f code/pom.xml spring-boot:run -Dspring-boot.run.arguments=--server.port=$env:APP_PORT
```

## Uso desde Karate

- El fichero `e2e/karate/src/test/resources/config-local.yml` apunta por defecto a `http://localhost:8080/products`.
- El feature `tests/products/getProductsLocal/getProductsLocal.feature` valida la respuesta esperada del micro.

Consulta `e2e/karate/README.md` para opciones de ejecución por tags/entornos y reportes.

## Notas

- Este módulo es opcional cuando se genera un proyecto con el arquetipo; se activa con `-DincludeCodeModule=true`.
- Nombre de la app: `karate-demo-micro` (propiedad `spring.application.name`).
