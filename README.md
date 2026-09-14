# VMETRIX DevOps Challenge

- Enunciado: [CHALLENGE.md](CHALLENGE.md)
- **Solução, arquitetura, decisões e como reproduzir: [SOLUTION.md](SOLUTION.md)**
- ADRs: [docs/adr/](docs/adr/)

| Módulo | Tipo | Pipeline |
|---|---|---|
| `calc-lib` | biblioteca | `ci.yml` → job `calc-lib` (`_lib.yml`) |
| `misc-utils` | biblioteca | `ci.yml` → job `misc-utils` (`_lib.yml`) |
| `svc-calc` | serviço :8082 | `ci.yml` → job `svc-calc` (`_service.yml`) → GitOps staging/production |
| `svc-misc` | serviço :8081 | `ci.yml` → job `svc-misc` (`_service.yml`) |
| `web-app` | web :8080 | `ci.yml` → job `web-app` (`_service.yml`) |
