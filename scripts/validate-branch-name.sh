#!/usr/bin/env bash

set -euo pipefail

branch_name="${1:-$(git rev-parse --abbrev-ref HEAD)}"
pattern='^(feature|fix|chore|docs|refactor|test|ci|build|perf)/(([A-Z]+-[0-9]+-)?[a-z0-9]+(-[a-z0-9]+)*)$'

if [[ "${branch_name}" == "HEAD" ]]; then
  echo "Detached HEAD detected. Skipping branch-name validation." >&2
  exit 0
fi

if [[ "${branch_name}" =~ ${pattern} ]]; then
  exit 0
fi

echo "Invalid branch name: ${branch_name}" >&2
echo "Expected format: type/TICKET-short-description or type/short-description" >&2
echo "Allowed types: feature, fix, chore, docs, refactor, test, ci, build, perf" >&2
exit 1
