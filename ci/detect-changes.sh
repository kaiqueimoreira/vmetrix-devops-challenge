#!/usr/bin/env bash
# Detecta quais módulos precisam rodar no pipeline comparando BASE..HEAD.
#
# Para cada módulo escreve duas saídas em $GITHUB_OUTPUT:
#   <modulo>=true|false      -> o job do módulo deve rodar
#   <modulo>-src=true|false  -> o código do próprio módulo mudou
#                               (usado pelo gate de versão das bibliotecas)
#
# Mudanças em infraestrutura compartilhada (workflows, ci/, docker/) fazem
# todos os módulos rodarem, mas NÃO marcam <modulo>-src: revalidar o pipeline
# não exige bump de versão de biblioteca.
#
# Uso: detect-changes.sh <base-sha> [head-sha] [force-all]
set -euo pipefail

MODULES=(calc-lib misc-utils svc-calc svc-misc web-app)
BASE="${1:-}"
HEAD="${2:-HEAD}"
FORCE_ALL="${3:-false}"
OUT="${GITHUB_OUTPUT:-/dev/stdout}"

# Sem base utilizável (branch nova, primeiro push, dispatch sem base): roda tudo.
if [[ -z "$BASE" || "$BASE" =~ ^0+$ ]] || ! git cat-file -e "${BASE}^{commit}" 2>/dev/null; then
  echo "Base '${BASE}' indisponível: todos os módulos serão executados."
  FORCE_ALL=true
fi

changed=""
if [[ "$FORCE_ALL" != "true" ]]; then
  changed="$(git diff --name-only "$BASE" "$HEAD")"
  echo "Arquivos alterados em ${BASE:0:7}..${HEAD:0:7}:"
  while IFS= read -r f; do echo "  $f"; done <<<"${changed:-<nenhum>}"
fi

shared_changed=false
if grep -qE '^(\.github/|ci/|docker/)' <<<"$changed"; then
  shared_changed=true
fi

for m in "${MODULES[@]}"; do
  src=false
  if grep -qE "^${m}/" <<<"$changed"; then src=true; fi

  run=$src
  if [[ "$FORCE_ALL" == "true" || "$shared_changed" == "true" ]]; then run=true; fi

  echo "${m}=${run}" >>"$OUT"
  echo "${m}-src=${src}" >>"$OUT"
  printf '  %-12s run=%-5s src-changed=%s\n' "$m" "$run" "$src"
done

deploy=false
if [[ "$FORCE_ALL" == "true" ]] || grep -qE '^(deploy/|\.github/)' <<<"$changed"; then deploy=true; fi
echo "deploy=${deploy}" >>"$OUT"
printf '  %-12s run=%s\n' "deploy" "$deploy"
