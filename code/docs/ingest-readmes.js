#!/usr/bin/env node
/**
 * Script: ingest-readmes.js
 * Objetivo: Leer README.md existentes en el repo y generar páginas AsciiDoc
 *           que incrustan el HTML resultante del Markdown convertido con 'marked'.
 *
 * Mapeos:
 *   ../../README.md                -> modules/ROOT/pages/index.adoc
 *   ../../e2e/karate/README.md     -> modules/karate/pages/index.adoc
 *   ../../code/README.md           -> modules/microservice/pages/index.adoc
 *   ../../code/archetype-karate-e2e/README.md -> modules/archetype/pages/index.adoc
 *
 * Notas:
 * - Si un README no existe, se informa y se continúa sin error.
 * - Se detecta el primer encabezado Markdown como título; si no existe, se define uno por defecto.
 * - El HTML se inserta dentro de un bloque passthrough AsciiDoc (++++ ... ++++) para conservar formato.
 */

const fs = require('fs');
const path = require('path');
const { marked } = require('marked');

// Fuente -> destino
const mappings = [
  {
    source: path.resolve(__dirname, '../../README.md'),
    dest: path.resolve(__dirname, 'modules/ROOT/pages/index.adoc'),
    fallbackTitle: 'Inicio'
  },
  {
    source: path.resolve(__dirname, '../../e2e/karate/README.md'),
    dest: path.resolve(__dirname, 'modules/karate/pages/index.adoc'),
    fallbackTitle: 'Karate E2E'
  },
  {
    source: path.resolve(__dirname, '../../code/README.md'),
    dest: path.resolve(__dirname, 'modules/microservice/pages/index.adoc'),
    fallbackTitle: 'Microservicio Demo'
  },
  {
    source: path.resolve(__dirname, '../../code/archetype-karate-e2e/README.md'),
    dest: path.resolve(__dirname, 'modules/archetype/pages/index.adoc'),
    fallbackTitle: 'Arquetipo Karate E2E'
  }
    ,
    {
      source: path.resolve(__dirname, './README.md'),
      dest: path.resolve(__dirname, 'modules/docs/pages/index.adoc'),
      fallbackTitle: 'Documentación (Docs)'
    }
];

function ensureDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

function extractTitle(markdown, fallback) {
  const lines = markdown.split(/\r?\n/);
  for (const line of lines) {
    const m = /^#\s+(.+)/.exec(line.trim());
    if (m) return m[1].trim();
  }
  return fallback;
}

function convertOne({ source, dest, fallbackTitle }) {
  if (!fs.existsSync(source)) {
    console.info(`[ingest] README no encontrado, se omite: ${source}`);
    return;
  }
  // Si el archivo destino existe y no contiene el marcador de generación, no sobrescribir
  // Aceptamos el marcador antiguo y el nuevo para permitir migración sin intervención
  const OLD_GENERATED_MARKER = '//// GENERATED-BY: ingest-readmes.js ////';
  const GENERATED_MARKER = '// GENERATED-BY: ingest-readmes.js'; // comentario válido en AsciiDoc (no se renderiza)
  if (fs.existsSync(dest)) {
    const current = fs.readFileSync(dest, 'utf-8');
    if (!(current.includes(GENERATED_MARKER) || current.includes(OLD_GENERATED_MARKER))) {
      console.info(`[ingest] Archivo existente sin marcador, se omite para preservar ediciones manuales: ${dest}`);
      return;
    }
  }
  const md = fs.readFileSync(source, 'utf-8');
  const title = extractTitle(md, fallbackTitle);
  const html = marked.parse(md);

  const adoc = [
    `= ${title}`,
    ':page-component: auto-karate',
    GENERATED_MARKER,
    '',
    '++++',
    html.trim(),
    '++++',
    ''
  ].join('\n');

  ensureDir(path.dirname(dest));
  fs.writeFileSync(dest, adoc, 'utf-8');
  console.info(`[ingest] Generado ${dest}`);
}

(function main() {
  console.info('[ingest] Iniciando ingesta de READMEs...');
  mappings.forEach(convertOne);
  console.info('[ingest] Proceso completado.');
})();
