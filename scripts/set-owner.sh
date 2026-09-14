#!/usr/bin/env bash
# Aponta os manifests (imagens GHCR e repoURL do ArgoCD) para outro dono do GitHub.
# Necessário uma única vez ao fazer fork do repositório.
# Uso: scripts/set-owner.sh <usuario-ou-org-github>
set -euo pipefail
OWNER="$(tr '[:upper:]' '[:lower:]' <<<"${1:?usuário/org do GitHub}")"
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
grep -rlE 'ghcr\.io/|github\.com/' "$ROOT_DIR/deploy" | while read -r f; do
  sed -i.bak -E \
    -e "s#ghcr\.io/[^/]+/#ghcr.io/${OWNER}/#g" \
    -e "s#github\.com/[^/]+/vmetrix-devops-challenge#github.com/${OWNER}/vmetrix-devops-challenge#g" \
    "$f" && rm -f "$f.bak"
  echo "atualizado: ${f#"$ROOT_DIR"/}"
done
