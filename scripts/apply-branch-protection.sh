#!/usr/bin/env bash

set -euo pipefail

owner="${1:-jonisf87}"
repo="${2:-fwk-karateautomulti-next}"
branch="${3:-main}"
ruleset_name="default-branch-codeowners-admin-bypass"
admin_role_actor_id=2

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
  "enforce_admins": false,
  "required_pull_request_reviews": {
    "dismiss_stale_reviews": true,
    "require_code_owner_reviews": true,
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

tmp_ruleset_payload="$(mktemp)"
trap 'rm -f "${tmp_payload}" "${tmp_ruleset_payload}"' EXIT

cat > "${tmp_ruleset_payload}" <<JSON
{
  "name": "${ruleset_name}",
  "target": "branch",
  "enforcement": "active",
  "bypass_actors": [
    {
      "actor_id": ${admin_role_actor_id},
      "actor_type": "RepositoryRole",
      "bypass_mode": "pull_request"
    }
  ],
  "conditions": {
    "ref_name": {
      "include": ["~DEFAULT_BRANCH"],
      "exclude": []
    }
  },
  "rules": [
    {
      "type": "pull_request",
      "parameters": {
        "dismiss_stale_reviews_on_push": true,
        "require_code_owner_review": true,
        "require_last_push_approval": false,
        "required_approving_review_count": 1,
        "required_review_thread_resolution": true,
        "allowed_merge_methods": ["squash", "rebase"]
      }
    }
  ]
}
JSON

existing_ruleset_id="$(
  gh api \
    -H "Accept: application/vnd.github+json" \
    "repos/${owner}/${repo}/rulesets" \
    --jq ".[] | select(.name == \"${ruleset_name}\") | .id" \
    | head -n 1
)"

if [[ -n "${existing_ruleset_id}" ]]; then
  gh api \
    --method PATCH \
    -H "Accept: application/vnd.github+json" \
    -H "X-GitHub-Api-Version: 2022-11-28" \
    "repos/${owner}/${repo}/rulesets/${existing_ruleset_id}" \
    --input "${tmp_ruleset_payload}" \
    >/dev/null
else
  gh api \
    --method POST \
    -H "Accept: application/vnd.github+json" \
    -H "X-GitHub-Api-Version: 2022-11-28" \
    "repos/${owner}/${repo}/rulesets" \
    --input "${tmp_ruleset_payload}" \
    >/dev/null
fi

echo "Repository merge policy, branch protection and codeowners ruleset applied to ${owner}/${repo}:${branch}"
