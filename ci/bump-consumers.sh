#!/usr/bin/env bash
# Atualiza a propriedade <LIB.version> em todos os poms que consomem a
# biblioteca. Os consumidores são descobertos pelo próprio pom (quem declara
# a propriedade), então um novo serviço entra no fluxo sem mudar o pipeline.
#
# Uso: bump-consumers.sh <biblioteca> <nova-versao>
# Imprime a lista de módulos alterados.
set -euo pipefail

LIB="$1"
VERSION="$2"
PROP="${LIB}.version"

for pom in */pom.xml; do
  grep -q "<${PROP}>" "$pom" || continue
  current="$(yq -p=xml -oy ".project.properties.\"${PROP}\"" "$pom")"
  if [[ "$current" != "$VERSION" ]]; then
    # sed em vez de yq -i para preservar formatação e comentários do pom.
    sed -i.bak -E "s|<${PROP}>[^<]*</${PROP}>|<${PROP}>${VERSION}</${PROP}>|" "$pom"
    rm -f "${pom}.bak"
    dirname "$pom"
  fi
done
