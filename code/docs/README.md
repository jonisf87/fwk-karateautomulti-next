# Workspace Antora

Directorio de la documentacion Antora del repositorio.

## Overview

Contiene:

* playbooks de desarrollo y publicacion
* paginas AsciiDoc mantenidas manualmente
* ingesta auxiliar desde README
* overrides visuales en `ui/supplemental/`
* empaquetado local de la UI con `scripts/bundle-ui.js`

## Requirements

* Node.js 18 o superior

## Installation

```bash
cd code/docs
npm ci
```

## Execution

Build local de desarrollo:

```bash
npm run build:dev
npm run preview
```

Build de produccion:

```bash
npm run ui:bundle
npm run build:prod
```

Actualizar snapshots desde README:

```bash
npm run ingest
```

## Structure

```text
code/docs/
├── antora.yml
├── antora-playbook.dev.yml
├── antora-playbook.yml
├── ingest-readmes.js
├── modules/
├── scripts/
└── ui/supplemental/
```

## Contribution

* Mantener la home y las paginas principales como contenido editorial Antora.
* Usar `ingest-readmes.js` solo como referencia auxiliar desde README.
* No tocar bundles minificados en `ui-bundle-extract/` ni `ui-bundle/`.
