# ADR-002 — Distribuição das bibliotecas: GitHub Packages + artifact da execução

- **Status:** aceito
- **Data:** 2026-09-14

## Contexto

Os serviços precisam resolver `calc-lib` e `misc-utils` "do passo anterior", nunca de um
`mvn install` manual. O desafio aceita tanto artifacts do pipeline quanto um registry real.

Dois cenários precisam funcionar:

1. **Lib já publicada**: um PR mexe só no `svc-calc`, que usa `calc-lib 1.0.0` publicada meses atrás.
2. **Lib + consumidor no mesmo PR**: alguém muda `calc-lib` para `1.1.0` e ajusta `svc-calc` para
   usar a nova API. A `1.1.0` ainda não existe em registry nenhum.

## Opções consideradas

| Opção | Cenário 1 | Cenário 2 | Observações |
|---|---|---|---|
| **Só artifacts do Actions** | ❌ artifacts expiram (máx. 90 dias) e baixar de *outro* run exige descobrir `run-id` via API | ✅ | Não é um repositório de verdade: sem imutabilidade de versão, sem consumo local por devs |
| **Nexus/Artifactory próprio** | ✅ | ❌ (mesmo problema) | Mais infra para operar e expor ao runner; fora do escopo |
| **GitHub Packages (Maven)** | ✅ | ❌ sozinho | Integrado ao repo, autenticado por `GITHUB_TOKEN` efêmero, **versões imutáveis** (republicar a mesma versão retorna 409) |
| **GitHub Packages + artifact da execução** | ✅ | ✅ | Escolhida |

## Decisão

- Na **main**, cada biblioteca com versão nova é publicada no **GitHub Packages** (`mvn deploy`
  com `-DaltDeploymentRepository`, sem alterar o pom) e ganha a tag git `<lib>-v<versão>`.
- Em **toda execução** (PR, develop, main), o job da lib também sobe o JAR + pom como artifact
  `lib-<modulo>`. Os jobs de serviço baixam esses artifacts e instalam no `~/.m2` do runner
  (`ci/install-run-libs.sh`). O que não foi construído nesta execução vem do GitHub Packages.
- `ci/lib-version-gate.sh` torna as versões imutáveis na prática: se o código da lib mudou e a
  versão do pom já está publicada, o pipeline **falha já no PR** pedindo bump.
- O repositório e as credenciais Maven do CI ficam em `.github/maven-settings.xml`, lendo
  `GITHUB_ACTOR`/`GITHUB_TOKEN` do ambiente.

## Consequências

- (+) Nenhum JAR não testado é publicado: `deploy` só roda depois de `compile` e `test` no mesmo job.
- (+) Rastreabilidade: versão ↔ tag git ↔ run que publicou.
- (−) GitHub Packages exige autenticação até para leitura de pacotes públicos. Devs locais
  precisam de um PAT com `read:packages` (documentado no SOLUTION.md) ou `mvn install` das libs.
- (−) Lock-in moderado no GitHub. A migração para Nexus/Artifactory é trocar a URL no
  `maven-settings.xml` e o destino do `altDeploymentRepository`.
