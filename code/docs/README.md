# Documentación con Antora (code/docs)

Este directorio contiene el sitio de documentación generado con Antora 3.
Incluye ingesta automática de READMEs y un modo de desarrollo offline. La búsqueda ha sido eliminada.

## Requisitos

- Node.js 18+ (recomendado 18.x o 20.x)
- No necesitas instalar Antora globalmente; se usa vía `npx` con las dependencias locales.

## Scripts disponibles

Desde `code/docs/`:

- `npm run ingest`: Convierte los README.md a páginas AsciiDoc (módulos ROOT, karate, microservice, archetype y este `docs`).
- `npm run build` (producción con fetch): Construye con el playbook principal y `--fetch` (no necesario en entornos cerrados).
- `npm run build:prod` (producción local): Construye con `antora-playbook.yml` usando `ui/ui-bundle.zip`.
- `npm run build:dev` (desarrollo/offline): Construye con `antora-playbook.dev.yml` sin descargas (usa UI extraída).
- `npm run preview`: Construye en modo dev y sirve el sitio en `http://localhost:5080`.
- `npm run watch`: Vuelve a construir en modo dev al detectar cambios en módulos, playbooks y READMEs mapeados.
- `npm run preview`: Servidor local del sitio (5080).
- `npm run clean`: Elimina `build/` y `.antora-cache/`.
- `npm run ui:bundle`: Genera `ui/ui-bundle.zip` inyectando overrides sobre `ui/ui-bundle-extract/`.

## Modo offline (recomendado en entornos corporativos)

El playbook de desarrollo (`antora-playbook.dev.yml`) está configurado con `runtime.fetch: false` y usa la carpeta de UI extraída `./ui/ui-bundle-extract` para iteración rápida.

1) Para prod determinista, genera el ZIP de UI:

```bash
cd code/docs
npm install
npm run ui:bundle
```

2) Ejecuta:

```bash
npm run build:dev
npm run preview
```

## Estructura de módulos del sitio

- ROOT: Página principal del proyecto.
- karate: Detalles del módulo E2E (Karate tests).
- microservice: Detalles del microservicio de ejemplo (`code`).
- archetype: Arquetipo Maven para generar proyectos E2E.
- docs: Esta página con instrucciones del sitio Antora.

## Ingesta de READMEs

El script `ingest-readmes.js` convierte markdown → HTML y lo inserta en páginas AsciiDoc con bloque passthrough (`++++`).
- Añade un comentario de control `// GENERATED-BY: ingest-readmes.js` para reconocer páginas generadas automáticamente.
- Si una página fue editada manualmente y no contiene el comentario, no se sobreescribe.

Fuentes mapeadas actualmente:
- `../../README.md` → `modules/ROOT/pages/index.adoc`
- `../../e2e/karate/README.md` → `modules/karate/pages/index.adoc`
- `../../code/README.md` → `modules/microservice/pages/index.adoc`
- `../../code/archetype-karate-e2e/README.md` → `modules/archetype/pages/index.adoc`
- `./README.md` → `modules/docs/pages/index.adoc`

## Solución de problemas

- “UI bundle inválido o no encontrado”: Verifica que `ui/ui-bundle.zip` existe (si usas prod) o que `ui/ui-bundle-extract` existe (si usas dev).
- “Cambios no se reflejan”: Ejecuta `npm run ingest` y limpia la caché de Antora si fuera necesario (`npm run clean`).
- WSL/DNS: Si tienes cortes de red, usa el modo offline; no dependemos ya de bundles remotos.

## Salida

- El sitio renderizado se genera en `code/docs/build/site/`.
