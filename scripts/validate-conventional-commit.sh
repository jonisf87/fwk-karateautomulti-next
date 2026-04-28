#!/usr/bin/env bash

set -euo pipefail

if [[ $# -gt 0 && -f "${1}" ]]; then
  commit_message="$(cat "${1}")"
elif [[ $# -gt 0 ]]; then
  commit_message="${1}"
else
  commit_message="$(cat)"
fi

commit_message="${commit_message//$'\r'/}"
subject_line="$(printf '%s\n' "${commit_message}" | head -n 1)"
type_pattern='(feat|fix|test|refactor|chore|docs|perf|ci|build)'

if [[ -z "${subject_line}" ]]; then
  echo "Commit message cannot be empty." >&2
  exit 1
fi

if (( ${#subject_line} > 72 )); then
  echo "Commit subject exceeds 72 characters: ${subject_line}" >&2
  exit 1
fi

if [[ ! "${subject_line}" =~ ^${type_pattern}(\([a-z0-9._/-]+\))?!?:\ .+$ ]]; then
  echo "Invalid Conventional Commit header: ${subject_line}" >&2
  echo "Expected: type(scope): subject" >&2
  exit 1
fi

subject_tail="${subject_line#*: }"

if [[ "${subject_tail}" =~ \.$ ]]; then
  echo "Commit subject must not end with a period: ${subject_line}" >&2
  exit 1
fi

if [[ ! "${subject_tail}" =~ ^[a-z0-9] ]]; then
  echo "Commit subject must start in lowercase: ${subject_line}" >&2
  exit 1
fi

if [[ "${subject_tail}" =~ [A-Z] ]]; then
  echo "Commit subject must be lowercase: ${subject_line}" >&2
  exit 1
fi
