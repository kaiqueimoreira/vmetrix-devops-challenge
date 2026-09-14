# ADR-004 — GitOps: CI escreve a tag no overlay; produção com gate duplo e sync manual

- **Status:** aceito
- **Data:** 2026-09-14

## Contexto

Requisitos do ArgoCD: sincronização automática a cada merge na `main`, pelo menos dois ambientes,
aprovação explícita para produção e rollback sem novo build.

## Opções consideradas

**Como a nova tag chega ao Git**

| Opção | Avaliação |
|---|---|
| ArgoCD Image Updater | Mais um controller no cluster com credencial de escrita no Git; o "quem aprovou" fica fora do Git/Actions |
| Tag `latest` / tag mutável + `imagePullPolicy: Always` | Proibido pelo desafio e não rastreável; rollback impossível |
| **CI faz commit da tag imutável no overlay** (`kustomization.yaml → images.newTag`) | ✅ Simples, auditável (`git log deploy/`), roda com o `GITHUB_TOKEN` do run |

**Como aprovar produção**

| Opção | Avaliação |
|---|---|
| Auto-sync em produção + PR manual de promoção | Aprovação = review do PR; funciona, mas o momento do deploy é o merge |
| **GitHub Environment `production` com revisor obrigatório + Application de produção sem auto-sync** | ✅ Escolhida |

## Decisão

- **Staging** (`svc-calc-staging`): `automated: {prune, selfHeal}`. O job `deploy staging` do
  `ci.yml` grava a tag nova após o push da imagem ⇒ ArgoCD aplica sozinho.
- **Produção** (`svc-calc-production`): **sem** `automated`.
  - **Gate 1 — o que vai para produção:** o workflow `promote-production` roda no Environment
    `production` e só continua depois de aprovado por um revisor. Ele confirma que a imagem existe
    no GHCR (nenhum build) e grava a tag no overlay de produção.
  - **Gate 2 — quando aplicar:** a Application fica `OutOfSync` até alguém com o papel
    `production-deployer` do AppProject executar Sync.
- **Rollback sem build**, três caminhos, do mais recomendado ao emergencial:
  1. `promote-production` com `tag=<tag anterior>`, que deixa rastro no Git;
  2. `git revert` do commit `deploy(svc-calc/production): ...`;
  3. `argocd app rollback svc-calc-production <history-id>`. Só é possível porque produção não
     tem auto-sync; depois é preciso alinhar o Git com (1) ou (2).
- Imagens nunca são sobrescritas: tag = `<versão-pom>-<sha7>` e também `sha-<sha completo>`.

## Consequências

- (+) Git é a fonte da verdade do que roda em cada ambiente; `git log` responde "quem, quando, o quê".
- (+) Rollback em segundos, sem depender do CI estar saudável (opção 3).
- (−) O bot faz push direto na `main` só em `deploy/**`. Com proteção de branch exigindo PR,
  o bot precisa estar na lista de bypass do ruleset. A alternativa sem bypass é abrir PR
  auto-mergeável, que é mais lento.
- (−) Gate duplo pode parecer redundante. Ele separa **aprovação de conteúdo** (auditada no
  GitHub) de **janela de execução** (operador no ArgoCD). Se o time preferir, basta ligar o
  auto-sync em produção e manter só o Gate 1.
