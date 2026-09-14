#!/usr/bin/env bash
# Remove runs "pull_request" presos de PRs abertos pelo próprio pipeline (github-actions[bot]).
#
# Quando o GITHUB_TOKEN abre um PR, o GitHub cria um run de `pull_request` que nunca executa
# (0 jobs, aguardando aprovação). A validação real desses PRs é o run disparado via
# workflow_dispatch no mesmo commit. O run preso só polui a lista do Actions e, ao apagar a
# branch no merge, termina como "failure". Este script cancela/apaga apenas runs com 0 jobs.
#
# Nunca falha o job chamador: é limpeza cosmética.
# Uso: cleanup-bot-pr-runs.sh <branch> [segundos-de-espera]
set -uo pipefail

BRANCH="${1:?branch}"
WAIT="${2:-30}"
REPO="${GITHUB_REPOSITORY:?}"
deadline=$((SECONDS + WAIT))
removed=0

while :; do
  pending=0
  ids="$(gh api -X GET "repos/${REPO}/actions/runs" -f branch="$BRANCH" -f event=pull_request \
    -q '.workflow_runs[].id' 2>/dev/null || true)"

  for id in $ids; do
    jobs="$(gh api "repos/${REPO}/actions/runs/${id}/jobs" -q '.total_count' 2>/dev/null || echo 1)"
    [[ "$jobs" == "0" ]] || continue   # run com jobs reais: não mexe

    if gh api -X DELETE "repos/${REPO}/actions/runs/${id}" >/dev/null 2>&1; then
      echo "Run pull_request preso ${id} (0 jobs) removido."
      removed=$((removed + 1))
    else
      # Runs ainda não concluídos não podem ser apagados: cancela e tenta de novo no próximo ciclo.
      gh api -X POST "repos/${REPO}/actions/runs/${id}/cancel" >/dev/null 2>&1 || true
      pending=$((pending + 1))
    fi
  done

  if (( pending == 0 && (removed > 0 || SECONDS >= deadline) )) || (( SECONDS >= deadline )); then
    break
  fi
  sleep 3
done

echo "Limpeza de runs presos em ${BRANCH}: ${removed} removido(s), ${pending} pendente(s)."
exit 0
