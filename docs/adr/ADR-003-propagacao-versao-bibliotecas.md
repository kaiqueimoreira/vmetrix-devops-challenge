# ADR-003 — Propagação de nova versão de biblioteca: versões fixas + PR automático de bump

- **Status:** aceito
- **Data:** 2026-09-14

## Contexto

"Cuando una biblioteca cambia de versión, todos sus consumidores deben actualizarse y pasar por
el pipeline antes de llegar a producción." Os poms dos serviços já declaram a versão das libs em
propriedades (`<calc-lib.version>1.0.0</calc-lib.version>`).

## Opções consideradas

1. **SNAPSHOT / version ranges** (`[1.0,2.0)`): consumidores pegam "a última".
   Build não reprodutível: o mesmo commit do serviço gera artefatos diferentes em dias diferentes,
   e uma lib quebrada entra em produção sem que o serviço tenha mudado. ❌
2. **Rebuild automático de todos os consumidores** a cada publicação de lib, sem mudar o pom deles.
   Se o pom continua apontando para a versão antiga, rebuildar não prova nada. ❌
3. **Renovate/Dependabot** lendo o GitHub Packages.
   Bom em escala, mas é mais uma ferramenta com credenciais e agenda própria, e o PR não sai
   imediatamente após a publicação. Fica como evolução.
4. **Versões fixas + PR de bump aberto pelo próprio pipeline** logo após a publicação. ✅

## Decisão

Opção 4:

1. Merge na `main` altera `calc-lib` com versão nova ⇒ job `calc-lib` compila, testa, publica
   e cria a tag.
2. Job `bump-calc-lib` (só roda se **publicou**) executa `ci/bump-consumers.sh`. O script descobre
   os consumidores pelos poms que declaram `<calc-lib.version>`, atualiza a propriedade, abre o PR
   `deps/calc-lib-<versão>` contra a `develop` e dispara o `ci.yml` nesse branch.
   Commits feitos com `GITHUB_TOKEN` não disparam workflows; `workflow_dispatch` é a exceção
   permitida.
3. O CI do PR detecta `svc-calc/pom.xml` e `web-app/pom.xml` alterados e roda
   `resolve → verify → docker build → smoke test` contra a nova versão.
4. Com `ci-result` verde e revisão, merge na `develop` ⇒ release para `main` ⇒ imagens novas ⇒
   staging automático ⇒ produção com aprovação.

**Garantia de ordem:** um consumidor não consegue compilar com `calc-lib 1.1.0` antes dela existir,
porque a resolução falha. Na mesma execução, `needs:` garante que a lib termina antes (ADR-001).
Em nenhum momento um serviço muda de versão de lib sem passar por CI e por PR.

## Consequências

- (+) Builds reprodutíveis; a imagem carrega o label `com.vmetrix.internal-libs=calc-lib:1.1.0`,
  e o resumo do job mostra quais versões de libs entraram.
- (+) Um consumidor que não compila com a nova versão fica com o PR vermelho. A lib não é
  "despublicada" e os outros consumidores seguem.
- (−) Uma mudança só de lib precisa de dois ciclos até a produção: lib na main, depois bump dos
  consumidores. Para mudanças coordenadas, o dev pode alterar lib + consumidores **no mesmo PR**;
  o artifact da execução cobre esse caso (ADR-002).
