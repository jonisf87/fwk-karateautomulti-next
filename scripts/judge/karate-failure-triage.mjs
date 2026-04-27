#!/usr/bin/env node

import fs from 'node:fs/promises';
import path from 'node:path';
import process from 'node:process';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const DEFAULT_MODEL = process.env.KARATE_JUDGE_MODEL?.trim() || 'gemini-2.5-flash-lite';
const DEFAULT_TIMEOUT_MS = parseInt(process.env.KARATE_JUDGE_TIMEOUT_MS?.trim() || '30000', 10);
const API_KEY = process.env.KARATE_JUDGE_API_KEY?.trim();

function parseArgs(argv) {
  const args = {};

  for (let index = 0; index < argv.length; index += 1) {
    const arg = argv[index];
    if (arg === '--input') {
      args.input = argv[index + 1];
      index += 1;
    } else if (arg === '--output') {
      args.output = argv[index + 1];
      index += 1;
    } else if (arg === '--model') {
      args.model = argv[index + 1];
      index += 1;
    }
  }

  return args;
}

function assertArgs(args) {
  if (!args.input) {
    throw new Error('Missing required argument: --input <path>');
  }
}

function assertApiKey() {
  if (!API_KEY) {
    throw new Error(
      'Missing KARATE_JUDGE_API_KEY. Store the provider token as a GitHub Actions secret and expose only the environment variable name in workflows.'
    );
  }
}

function validateJudgeOutput(result) {
  const requiredFields = [
    'category',
    'confidence',
    'summary',
    'evidence',
    'reasoning',
    'recommended_action',
    'annotation_level'
  ];
  const categories = new Set([
    'ENVIRONMENT_UNAVAILABLE',
    'DEMO_SERVICE_FAILURE',
    'FLAKY_TEST',
    'TEST_DEFECT',
    'UNKNOWN'
  ]);
  const confidenceLevels = new Set(['high', 'medium', 'low']);
  const annotationLevels = new Set(['error', 'warning', 'notice']);

  for (const fieldName of requiredFields) {
    if (!(fieldName in result)) {
      throw new Error(`Judge output is missing required field: ${fieldName}`);
    }
  }

  if (!categories.has(result.category)) {
    throw new Error(`Unsupported category: ${result.category}`);
  }

  if (!confidenceLevels.has(result.confidence)) {
    throw new Error(`Unsupported confidence: ${result.confidence}`);
  }

  if (!annotationLevels.has(result.annotation_level)) {
    throw new Error(`Unsupported annotation_level: ${result.annotation_level}`);
  }

  if (!Array.isArray(result.evidence) || result.evidence.length === 0 || result.evidence.length > 5) {
    throw new Error('Judge output evidence must be an array with 1 to 5 items.');
  }

  for (const evidenceItem of result.evidence) {
    if (typeof evidenceItem !== 'string' || evidenceItem.trim() === '') {
      throw new Error('Judge output evidence items must be non-empty strings.');
    }
  }
}

async function readJsonFile(filePath) {
  const raw = await fs.readFile(filePath, 'utf8');
  return JSON.parse(raw);
}

async function buildPromptPayload(inputPath) {
  const [promptText, schema, inputPayload] = await Promise.all([
    fs.readFile(path.join(__dirname, 'karate-failure-triage-prompt.md'), 'utf8'),
    readJsonFile(path.join(__dirname, 'karate-failure-triage-schema.json')),
    readJsonFile(inputPath)
  ]);

  return {
    promptText,
    schema,
    serializedInput: JSON.stringify(inputPayload, null, 2)
  };
}

async function callGemini(model, promptText, schema, serializedInput) {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), DEFAULT_TIMEOUT_MS);

  try {
    const response = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'x-goog-api-key': API_KEY
        },
        signal: controller.signal,
        body: JSON.stringify({
          contents: [
            {
              parts: [
                {
                  text: `${promptText}\n\n---\n\nInput payload (JSON):\n${serializedInput}`
                }
              ]
            }
          ],
          generationConfig: {
            responseMimeType: 'application/json',
            responseJsonSchema: schema
          }
        })
      }
    );

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Gemini API call failed (${response.status}): ${errorText}`);
    }

    const responseJson = await response.json();
    const responseText = responseJson?.candidates?.[0]?.content?.parts?.[0]?.text;

    if (!responseText) {
      throw new Error('Gemini API did not return candidate text.');
    }

    const parsed = JSON.parse(responseText);
    validateJudgeOutput(parsed);
    return parsed;
  } finally {
    clearTimeout(timeout);
  }
}

async function writeOutput(outputPath, result) {
  const serialized = `${JSON.stringify(result, null, 2)}\n`;
  if (outputPath) {
    await fs.writeFile(outputPath, serialized, 'utf8');
  } else {
    process.stdout.write(serialized);
  }
}

async function main() {
  const args = parseArgs(process.argv.slice(2));
  assertArgs(args);
  assertApiKey();

  const model = args.model ?? DEFAULT_MODEL;
  const { promptText, schema, serializedInput } = await buildPromptPayload(args.input);
  const result = await callGemini(model, promptText, schema, serializedInput);
  await writeOutput(args.output, result);
}

main().catch((error) => {
  console.error(error instanceof Error ? error.message : String(error));
  process.exit(1);
});
