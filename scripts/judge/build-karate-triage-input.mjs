#!/usr/bin/env node

import fs from 'node:fs/promises';
import path from 'node:path';
import process from 'node:process';

function parseArgs(argv) {
  const args = {};

  for (let index = 0; index < argv.length; index += 1) {
    const arg = argv[index];
    if (arg.startsWith('--')) {
      args[arg.slice(2)] = argv[index + 1];
      index += 1;
    }
  }

  return args;
}

function excerptText(text, maxLength = 8000) {
  if (!text) {
    return null;
  }

  if (text.length <= maxLength) {
    return text;
  }

  const headLength = Math.floor(maxLength / 2);
  const tailLength = maxLength - headLength;
  return `${text.slice(0, headLength)}\n...\n${text.slice(-tailLength)}`;
}

async function maybeReadText(filePath) {
  if (!filePath) {
    return null;
  }

  try {
    return await fs.readFile(filePath, 'utf8');
  } catch {
    return null;
  }
}

async function maybeReadJson(filePath) {
  const raw = await maybeReadText(filePath);
  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

async function maybeReadKarateJsonDirectory(directoryPath) {
  if (!directoryPath) {
    return null;
  }

  try {
    const entries = await fs.readdir(directoryPath, { withFileTypes: true });
    const jsonFiles = entries
      .filter((entry) => entry.isFile() && entry.name.endsWith('.json'))
      .map((entry) => path.join(directoryPath, entry.name))
      .sort();

    const parsedFiles = await Promise.all(jsonFiles.map((filePath) => maybeReadJson(filePath)));
    return parsedFiles.flatMap((item) => (Array.isArray(item) ? item : []));
  } catch {
    return null;
  }
}

function summarizeCucumberJson(cucumberJson) {
  if (!Array.isArray(cucumberJson)) {
    return {
      totalFeatures: 0,
      failedScenarios: [],
      rawAvailable: false
    };
  }

  const failedScenarios = [];

  for (const feature of cucumberJson) {
    const elements = Array.isArray(feature.elements) ? feature.elements : [];
    for (const element of elements) {
      const steps = Array.isArray(element.steps) ? element.steps : [];
      const failedStep = steps.find(
        (step) => step?.result?.status && step.result.status !== 'passed' && step.result.status !== 'skipped'
      );

      if (failedStep) {
        failedScenarios.push({
          feature: feature.name ?? feature.uri ?? 'unknown-feature',
          scenario: element.name ?? 'unknown-scenario',
          failed_step: failedStep.name ?? 'unknown-step',
          status: failedStep.result.status,
          error_message: failedStep.result.error_message ?? null
        });
      }
    }
  }

  return {
    totalFeatures: cucumberJson.length,
    failedScenarios,
    rawAvailable: true
  };
}

function extractSurefireSignals(surefireXmlText, surefireText) {
  const source = `${surefireXmlText ?? ''}\n${surefireText ?? ''}`;
  const failureMatches = [...source.matchAll(/<failure[^>]*message="([^"]*)"|<error[^>]*message="([^"]*)"/g)];
  const messages = failureMatches
    .map((match) => match[1] ?? match[2])
    .filter(Boolean)
    .slice(0, 10);

  return {
    hasFailureMarkers: /<failure|<error|Failures:\s+[1-9]|Errors:\s+[1-9]/.test(source),
    extractedMessages: messages
  };
}

async function main() {
  const args = parseArgs(process.argv.slice(2));

  if (!args.output) {
    throw new Error('Missing required argument: --output <path>');
  }

  const [
    demoLogText,
    healthcheckText,
    surefireXmlText,
    surefireText,
    cucumberJson,
    cucumberJsonFromDirectory,
    runMetadataJson
  ] = await Promise.all([
    maybeReadText(args.demoLog),
    maybeReadText(args.healthcheck),
    maybeReadText(args.surefireXml),
    maybeReadText(args.surefireTxt),
    maybeReadJson(args.cucumberJson),
    maybeReadKarateJsonDirectory(args.cucumberJsonDir),
    maybeReadJson(args.runMetadata)
  ]);

  const effectiveCucumberJson =
    Array.isArray(cucumberJsonFromDirectory) && cucumberJsonFromDirectory.length > 0
      ? cucumberJsonFromDirectory
      : cucumberJson;

  const payload = {
    repository: runMetadataJson?.repository ?? process.env.GITHUB_REPOSITORY ?? null,
    workflow: runMetadataJson?.workflow ?? process.env.GITHUB_WORKFLOW ?? null,
    job: runMetadataJson?.job ?? process.env.GITHUB_JOB ?? null,
    ref: runMetadataJson?.ref ?? process.env.GITHUB_REF_NAME ?? null,
    sha: runMetadataJson?.sha ?? process.env.GITHUB_SHA ?? null,
    karate_env: runMetadataJson?.karate_env ?? process.env.KARATE_ENV ?? null,
    app_port: runMetadataJson?.app_port ?? process.env.APP_PORT ?? null,
    run_metadata: runMetadataJson,
    service_healthcheck_excerpt: excerptText(healthcheckText, 2000),
    demo_service_log_excerpt: excerptText(demoLogText, 8000),
    surefire_xml_excerpt: excerptText(surefireXmlText, 8000),
    surefire_text_excerpt: excerptText(surefireText, 4000),
    surefire_signals: extractSurefireSignals(surefireXmlText, surefireText),
    cucumber_summary: summarizeCucumberJson(effectiveCucumberJson)
  };

  await fs.mkdir(path.dirname(args.output), { recursive: true });
  await fs.writeFile(args.output, `${JSON.stringify(payload, null, 2)}\n`, 'utf8');
}

main().catch((error) => {
  console.error(error instanceof Error ? error.message : String(error));
  process.exit(1);
});
