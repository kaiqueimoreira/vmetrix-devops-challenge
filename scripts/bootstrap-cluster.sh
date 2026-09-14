#!/usr/bin/env bash
# Sobe o ambiente GitOps local do zero:
#   1. cluster kind "vmetrix"
#   2. ArgoCD (versão fixa)
#   3. (opcional) secret de pull do GHCR, se as imagens estiverem privadas
#   4. app-of-apps "root" -> AppProject + Applications de staging e production
#
# Pré-requisitos: docker, kind, kubectl.  Opcional: argocd CLI.
# Variáveis:
#   GHCR_USER / GHCR_TOKEN  token com read:packages (só se os pacotes do GHCR forem privados)
#   ARGOCD_VERSION          default abaixo
set -euo pipefail

ARGOCD_VERSION="${ARGOCD_VERSION:-v2.14.11}"
CLUSTER_NAME="vmetrix"
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
NAMESPACES=(svc-calc-staging svc-calc-production)

log() { printf '\n\033[1;34m==> %s\033[0m\n' "$*"; }

for bin in docker kind kubectl; do
  command -v "$bin" >/dev/null || { echo "Faltando dependência: $bin" >&2; exit 1; }
done

if grep -rq '__GH_OWNER__' "$ROOT_DIR/deploy"; then
  echo "Os manifests ainda contêm o placeholder __GH_OWNER__. Rode scripts/set-owner.sh <usuario-github> antes." >&2
  exit 1
fi

log "Cluster kind '${CLUSTER_NAME}'"
if kind get clusters | grep -qx "$CLUSTER_NAME"; then
  echo "Cluster já existe, reutilizando."
else
  kind create cluster --config "$ROOT_DIR/scripts/kind-config.yaml" --wait 120s
fi
kubectl config use-context "kind-${CLUSTER_NAME}"

log "ArgoCD ${ARGOCD_VERSION}"
kubectl create namespace argocd --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -n argocd --server-side --force-conflicts \
  -f "https://raw.githubusercontent.com/argoproj/argo-cd/${ARGOCD_VERSION}/manifests/install.yaml"
kubectl -n argocd rollout status deploy/argocd-server --timeout=300s
kubectl -n argocd rollout status deploy/argocd-repo-server --timeout=300s
kubectl -n argocd rollout status statefulset/argocd-application-controller --timeout=300s

if [[ -n "${GHCR_TOKEN:-}" ]]; then
  log "Secret de pull do GHCR nos namespaces dos ambientes"
  for ns in "${NAMESPACES[@]}"; do
    kubectl create namespace "$ns" --dry-run=client -o yaml | kubectl apply -f -
    kubectl -n "$ns" create secret docker-registry ghcr-pull \
      --docker-server=ghcr.io --docker-username="${GHCR_USER:?defina GHCR_USER}" \
      --docker-password="$GHCR_TOKEN" --dry-run=client -o yaml | kubectl apply -f -
    kubectl -n "$ns" patch serviceaccount default \
      -p '{"imagePullSecrets":[{"name":"ghcr-pull"}]}'
  done
fi

log "App-of-apps (root)"
kubectl apply -f "$ROOT_DIR/deploy/argocd/bootstrap/root-app.yaml"

log "Aguardando Applications serem criadas"
for app in svc-calc-staging svc-calc-production; do
  for _ in $(seq 1 60); do
    kubectl -n argocd get application "$app" >/dev/null 2>&1 && break
    sleep 2
  done
done
kubectl -n argocd get applications

password="$(kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath='{.data.password}' | base64 -d)"
cat <<EOF

Pronto.
  UI do ArgoCD:  kubectl -n argocd port-forward svc/argocd-server 8443:443
                 https://localhost:8443  (usuário: admin / senha: ${password})
  CLI:           argocd login localhost:8443 --username admin --password '${password}' --insecure
  Staging:       kubectl -n svc-calc-staging port-forward svc/svc-calc 8082:80
                 curl localhost:8082/actuator/info
EOF
