#!/usr/bin/env node
const fs = require("fs");
const path = require("path");
const { execFileSync } = require("child_process");

const docsRoot = path.resolve(__dirname, "..");
const repoRoot = path.resolve(docsRoot, "../..");
const changelogPath = path.join(docsRoot, "modules", "ROOT", "pages", "changelog.adoc");

const beginMarker = "// BEGIN-GENERATED: generate-changelog.js";
const endMarker = "// END-GENERATED: generate-changelog.js";
const maxUnreleasedCommits = 80;

const categoryNames = {
  feat: "Nuevas capacidades",
  fix: "Correcciones",
  docs: "Documentacion",
  refactor: "Refactorizacion",
  test: "Tests",
  ci: "CI/CD",
  build: "Build y dependencias",
  chore: "Mantenimiento",
  perf: "Rendimiento",
  revert: "Reversiones",
  other: "Otros cambios",
};

const categoryOrder = [
  "feat",
  "fix",
  "docs",
  "refactor",
  "test",
  "ci",
  "build",
  "chore",
  "perf",
  "revert",
  "other",
];

function git(args) {
  return execFileSync("git", args, {
    cwd: repoRoot,
    encoding: "utf8",
    stdio: ["ignore", "pipe", "pipe"],
  }).trim();
}

function getRemoteUrl() {
  try {
    const remote = git(["config", "--get", "remote.origin.url"]);
    if (!remote) {
      return null;
    }
    if (remote.startsWith("git@github.com:")) {
      return remote.replace("git@github.com:", "https://github.com/").replace(/\.git$/, "");
    }
    if (remote.startsWith("https://github.com/")) {
      return remote.replace(/\.git$/, "");
    }
    return remote.replace(/\.git$/, "");
  } catch {
    return null;
  }
}

function getTags() {
  try {
    const output = git(["tag", "--sort=-creatordate", "--merged", "HEAD"]);
    return output ? output.split("\n").filter(Boolean) : [];
  } catch {
    return [];
  }
}

function getTagDate(tag) {
  return git(["log", "-1", "--date=short", "--pretty=format:%ad", tag]);
}

function getCommits(range, limit) {
  const args = ["log", "--no-merges", "--date=short", "--pretty=format:%H%x1f%ad%x1f%s"];
  if (typeof limit === "number") {
    args.push("-n", String(limit));
  }
  if (range) {
    args.push(range);
  }
  const output = git(args);
  if (!output) {
    return [];
  }
  return output
    .split("\n")
    .filter(Boolean)
    .map((line) => {
      const [hash, date, subject] = line.split("\x1f");
      return { hash, shortHash: hash.slice(0, 7), date, subject };
    });
}

function classifyCommit(subject) {
  const conventional = subject.match(/^([a-zA-Z]+)(\([^)]+\))?!?:\s+(.+)$/);
  if (conventional) {
    const type = conventional[1].toLowerCase();
    return {
      category: categoryNames[type] ? type : "other",
      summary: conventional[3],
    };
  }

  const lower = subject.toLowerCase();
  if (lower.startsWith("fix ")) {
    return { category: "fix", summary: subject };
  }
  if (lower.startsWith("add ") || lower.startsWith("feat ")) {
    return { category: "feat", summary: subject };
  }
  if (lower.startsWith("docs ") || lower.includes("readme") || lower.includes("antora")) {
    return { category: "docs", summary: subject };
  }
  if (lower.startsWith("refactor ") || lower.startsWith("align ") || lower.startsWith("normalize ")) {
    return { category: "refactor", summary: subject };
  }
  if (lower.startsWith("test ")) {
    return { category: "test", summary: subject };
  }
  if (lower.startsWith("ci ")) {
    return { category: "ci", summary: subject };
  }
  if (lower.startsWith("build ") || lower.includes("dependency") || lower.includes("dependencies")) {
    return { category: "build", summary: subject };
  }
  if (lower.startsWith("revert ")) {
    return { category: "revert", summary: subject };
  }
  if (lower.startsWith("chore ")) {
    return { category: "chore", summary: subject };
  }
  return { category: "other", summary: subject };
}

function formatCommit(commit, repoUrl) {
  const { category, summary } = classifyCommit(commit.subject);
  const link = repoUrl
    ? `link:${repoUrl}/commit/${commit.hash}[${commit.shortHash}]`
    : `\`${commit.shortHash}\``;
  return {
    category,
    line: `* ${summary} (${link}, ${commit.date})`,
  };
}

function renderSection(title, commits, repoUrl) {
  if (!commits.length) {
    return "";
  }

  const grouped = new Map(categoryOrder.map((key) => [key, []]));
  commits.forEach((commit) => {
    const formatted = formatCommit(commit, repoUrl);
    grouped.get(formatted.category).push(formatted.line);
  });

  const lines = [`=== ${title}`, ""];
  categoryOrder.forEach((key) => {
    const entries = grouped.get(key);
    if (!entries.length) {
      return;
    }
    lines.push(`==== ${categoryNames[key]}`, "");
    lines.push(...entries, "");
  });
  return lines.join("\n").trimEnd();
}

function buildSections(repoUrl) {
  const tags = getTags();
  const sections = [];

  if (tags.length) {
    const unreleased = getCommits(`${tags[0]}..HEAD`, maxUnreleasedCommits);
    if (unreleased.length) {
      sections.push(renderSection("Unreleased", unreleased, repoUrl));
    }

    tags.forEach((tag, index) => {
      const previousTag = tags[index + 1];
      const range = previousTag ? `${previousTag}..${tag}` : tag;
      const commits = getCommits(range);
      if (!commits.length) {
        return;
      }
      sections.push(renderSection(`${tag} (${getTagDate(tag)})`, commits, repoUrl));
    });
    return sections.filter(Boolean);
  }

  const commits = getCommits("HEAD", maxUnreleasedCommits);
  if (!commits.length) {
    return [];
  }
  return [renderSection("Unreleased", commits, repoUrl)];
}

function renderGeneratedBlock() {
  try {
    const repoUrl = getRemoteUrl();
    const sections = buildSections(repoUrl);
    if (!sections.length) {
      return [
        beginMarker,
        "// No edites entre estos marcadores; usa `npm run changelog`",
        "",
        "== Historico automatico",
        "",
        "No hay commits disponibles para construir el changelog.",
        endMarker,
      ].join("\n");
    }

    return [
      beginMarker,
      "// No edites entre estos marcadores; usa `npm run changelog`",
      "",
      "== Historico automatico",
      "",
      "Generado a partir del historial Git del repositorio. Para un resultado mas claro, usa commits con prefijos como `feat:`, `fix:` o `docs:` y crea tags de version (`v1.0.0`, `v1.1.0`, etc.).",
      "",
      sections.join("\n\n"),
      endMarker,
    ].join("\n");
  } catch (error) {
    return [
      beginMarker,
      "// No edites entre estos marcadores; usa `npm run changelog`",
      "",
      "== Historico automatico",
      "",
      `No es posible generar el historico automaticamente (${error.message}).`,
      endMarker,
    ].join("\n");
  }
}

function updateChangelog() {
  const current = fs.readFileSync(changelogPath, "utf8");
  const blockPattern = new RegExp(`${beginMarker}[\\s\\S]*?${endMarker}`, "m");
  if (!blockPattern.test(current)) {
    throw new Error("No se encontraron los marcadores de generacion del changelog.");
  }

  const updated = current.replace(blockPattern, renderGeneratedBlock());
  fs.writeFileSync(changelogPath, updated);
  console.log(`Updated ${path.relative(repoRoot, changelogPath)}`);
}

updateChangelog();
