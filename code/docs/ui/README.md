Este directorio contiene los artefactos de UI usados por Antora.

- `ui-bundle-extract/`: UI extraída con overrides directos (desarrollo).
- `ui-bundle.zip`: bundle empaquetado (producción determinista), generado por `npm run ui:bundle`.
- `supplemental/partials/`: overrides que se inyectan al zip y/o se aplican en la UI extraída.

Playbooks:
- Dev: `antora-playbook.dev.yml` usa `./ui/ui-bundle-extract`.
- Prod: `antora-playbook.yml` usa `./ui/ui-bundle.zip`.
