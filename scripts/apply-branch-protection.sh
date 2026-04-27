#!/usr/bin/env bash

set -euo pipefail

owner="${1:-jonisf87}"
repo="${2:-fwk-karateautomulti-next}"
branch="${3:-main}"

gh auth status >/dev/null

gh api \
  --method PATCH \
  -H "Accept: application/vnd.github+json" \
  "repos/${owner}/${repo}" \
  -f allow_squash_merge=true \
  -f allow_rebase_merge=true \
  -f allow_merge_commit=false \
  -f delete_branch_on_merge=false \
  >/dev/null

tmp_payload="$(mktemp)"
trap 'rm -f "${tmp_payload}"' EXIT

cat > "${tmp_payload}" <<'JSON'
{
  "required_status_checks": {
    "strict": true,
    "contexts": [
      "quality-gate",
      "mutation-testing",
      "karate-e2e",
      "karate-failure-triage",
      "conventional-commits",
      "branch-name-policy"
    ]
  },
  "enforce_admins": true,
  "required_pull_request_reviews": {
    "dismiss_stale_reviews": true,
    "require_code_owner_reviews": false,
    "required_approving_review_count": 1,
    "require_last_push_approval": false
  },
  "restrictions": null,
  "required_linear_history": true,
  "allow_force_pushes": false,
  "allow_deletions": false,
  "block_creations": false,
  "required_conversation_resolution": true,
  "lock_branch": false,
  "allow_fork_syncing": false
}
JSON

gh api \
  --method PUT \
  -H "Accept: application/vnd.github+json" \
  "repos/${owner}/${repo}/branches/${branch}/protection" \
  --input "${tmp_payload}" \
  >/dev/null

echo "Repository merge policy and branch protection applied to ${owner}/${repo}:${branch}"
