# ADR-001 — Monorepo com um pipeline orquestrador (DAG via `needs:`)

- **Status:** aceito
- **Data:** 2026-09-14

## Contexto

São 5 módulos com dependências entre si:

```
calc-lib   ──► svc-calc, web-app
misc-utils ──► svc-misc, web-app
```

O requisito central do desafio é garantir que, quando uma biblioteca muda, os consumidores
sejam construídos **depois** dela e **contra a versão correta**, antes de chegar à produção.
O time é pequeno e os módulos evoluem juntos.

## Opções consideradas

| # | Opção | Prós | Contras |
|---|---|---|---|
| A | **Multi-repo** (1 repo por módulo) + publicação de libs + Renovate/Dependabot nos consumidores | Isolamento total, permissões por repo | 5 repos para manter; ordem de build fica implícita (eventual); mudança lib+consumidor exige PRs coordenados em repos diferentes; mais difícil de demonstrar e de rastrear |
| B | **Monorepo, 1 workflow por módulo**, encadeados com `workflow_run` | Um arquivo por pipeline, fácil de ler isoladamente | `workflow_run` só funciona na branch default, não propaga contexto de PR, não compõe dependências múltiplas (web-app depende de 2 libs) e a ordem fica espalhada em N arquivos |
| C | **Monorepo, 1 workflow orquestrador** com detecção de mudanças e jobs por módulo ligados por `needs:`, reutilizando workflows `_lib.yml` / `_service.yml` | Grafo de dependências explícito e visível na UI; ordem garantida pelo runner; uma única execução mostra lib → serviços → deploy; mudança lib + consumidor validada no mesmo PR; um único check obrigatório (`ci-result`) | Arquivo orquestrador cresce com o número de módulos; um novo módulo exige adicionar um job (≈10 linhas) |
| D | Maven multi-module (reactor) com pom agregador | Ordem calculada pelo Maven | Exige alterar os poms (proibido salvo necessidade) e acopla versões de libs e serviços (contraria o requisito de versões independentes) |

## Decisão

**Opção C.**

- `ci/detect-changes.sh` calcula quais módulos mudaram (`git diff base..head`).
- Cada módulo é um job no `ci.yml`; serviços declaram `needs:` nas bibliotecas de que dependem.
  A condição `!failure() && !cancelled()` faz o serviço rodar se a lib passou **ou** foi pulada,
  e **nunca** se a lib falhou.
- A lógica de cada tipo de módulo fica em workflows reutilizáveis (`_lib.yml`, `_service.yml`),
  então "um pipeline por biblioteca/serviço" existe como job próprio, com logs, artifacts e
  resumo próprios, sem duplicar YAML.
- O job `ci-result` agrega todos os resultados e é o **único** status check exigido pela proteção
  de branch — jobs pulados por não haver mudança não bloqueiam PRs.

## Consequências

- (+) Ordem de build é uma propriedade do grafo, não uma convenção.
- (+) Na apresentação, um único run mostra ponta a ponta.
- (−) O grafo `needs:` é mantido à mão. Com dezenas de módulos, valeria gerar a matriz a partir
  dos poms (ver "Segunda iteração" no SOLUTION.md).
- (−) Mudanças em `.github/`, `ci/` ou `docker/` reconstroem todos os módulos (escolha
  conservadora: o pipeline mudou, então tudo é revalidado).
