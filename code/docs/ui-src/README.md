# UI build (antora-ui-default based)

This folder contains a local build that packages the current customized UI into a valid Antora UI bundle zip, without network access.

Scripts live in docs/package.json (at code/docs/package.json):
- npm run ui:bundle    # create ui/ui-bundle.zip (injects overrides)
- npm run build:dev    # build dev site using extracted UI
- npm run build:prod   # build prod site using zip UI

