#!/usr/bin/env bash
# Instala no repositório Maven local do runner as bibliotecas construídas
# NESTA MESMA execução do pipeline (baixadas como artifacts do job da lib).
#
# Isso permite validar numa única PR uma mudança de biblioteca + o bump dos
# consumidores, antes da biblioteca existir no GitHub Packages. Versões que
# não foram construídas nesta execução são resolvidas do GitHub Packages.
#
# Uso: install-run-libs.sh <diretorio-com-artifacts>
set -euo pipefail

DIR="${1:?diretório dos artifacts}"
shopt -s nullglob

found=0
for lib in "$DIR"/lib-*/; do
  pom="${lib}pom.xml"
  [[ -f "$pom" ]] || continue
  artifact="$(yq -p=xml -oy '.project.artifactId' "$pom")"
  version="$(yq -p=xml -oy '.project.version' "$pom")"
  jar="${lib}target/${artifact}-${version}.jar"
  echo "Instalando ${artifact}:${version} construída nesta execução (${jar})"
  mvn -B -q install:install-file -Dfile="$jar" -DpomFile="$pom"
  found=$((found + 1))
done

echo "${found} biblioteca(s) desta execução instalada(s); demais virão do GitHub Packages."
