#!/usr/bin/env bash
# Gate de versão de biblioteca: versões publicadas são imutáveis.
#
# - Se o código da biblioteca mudou e a versão do pom já existe no GitHub
#   Packages, falha: o desenvolvedor precisa fazer bump da versão.
# - Se a versão já existe e o código não mudou (ex.: só o pipeline mudou),
#   não falha, apenas marca publish=false (execução idempotente).
# - Versões -SNAPSHOT não são aceitas na main.
#
# Saídas ($GITHUB_OUTPUT): version, artifact, exists, publish
# Uso: lib-version-gate.sh <modulo> <src-changed:true|false>
set -euo pipefail

MODULE="$1"
SRC_CHANGED="$2"
OUT="${GITHUB_OUTPUT:-/dev/stdout}"
POM="${MODULE}/pom.xml"

GROUP_ID="$(yq -p=xml -oy '.project.groupId' "$POM")"
ARTIFACT_ID="$(yq -p=xml -oy '.project.artifactId' "$POM")"
VERSION="$(yq -p=xml -oy '.project.version' "$POM")"

if [[ "${GITHUB_REF:-}" == "refs/heads/main" && "$VERSION" == *-SNAPSHOT ]]; then
  echo "::error file=${POM}::Versão SNAPSHOT (${VERSION}) não pode ser publicada a partir da main."
  exit 1
fi

url="https://maven.pkg.github.com/${GITHUB_REPOSITORY}/${GROUP_ID//.//}/${ARTIFACT_ID}/${VERSION}/${ARTIFACT_ID}-${VERSION}.pom"
code="$(curl -s -o /dev/null -w '%{http_code}' -u "${GITHUB_ACTOR}:${GITHUB_TOKEN}" "$url" || true)"
echo "Consulta ${GROUP_ID}:${ARTIFACT_ID}:${VERSION} no GitHub Packages -> HTTP ${code}"

exists=false
[[ "$code" == "200" ]] && exists=true

publish=true
if [[ "$exists" == "true" ]]; then
  publish=false
  if [[ "$SRC_CHANGED" == "true" ]]; then
    echo "::error file=${POM}::${ARTIFACT_ID} ${VERSION} já está publicada e o código da biblioteca mudou. Faça bump de <version> no ${POM}."
    exit 1
  fi
  echo "Versão ${VERSION} já publicada e código inalterado: publicação será ignorada."
fi

{
  echo "version=${VERSION}"
  echo "artifact=${ARTIFACT_ID}"
  echo "exists=${exists}"
  echo "publish=${publish}"
} >>"$OUT"
