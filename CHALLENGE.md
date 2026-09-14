# Desafío Técnico — Ingeniero DevOps Senior
**VMETRIX · Proceso de Selección 2026**

---

## El Sistema que Vas a Operar

VMETRIX mantiene un sistema compuesto por **cuatro componentes Java** que comparten dos bibliotecas internas. Todos están construidos con Maven y Spring Boot 3.

```
misc-utils   (biblioteca)  ──┬──► svc-misc   (microservicio, puerto 8081)
                              └──► web-app    (aplicación web,  puerto 8080)

calc-lib     (biblioteca)  ──┬──► svc-calc   (microservicio, puerto 8082)
                              └──► web-app    (aplicación web,  puerto 8080)
```

| Módulo | Tipo | Descripción |
|---|---|---|
| `misc-utils` | Biblioteca JAR | Utilidades de strings, fechas y validación (CPF, email) |
| `calc-lib` | Biblioteca JAR | Cálculos financieros y estadística descriptiva |
| `svc-misc` | Microservicio REST | Expone `misc-utils` como API REST (puerto 8081) |
| `svc-calc` | Microservicio REST | Expone `calc-lib` como API REST (puerto 8082) |
| `web-app` | Aplicación Web | Portal Thymeleaf que usa ambas bibliotecas (puerto 8080) |

Las bibliotecas son consumidas por los microservicios y por la aplicación web. Cuando una biblioteca cambia de versión, todos sus consumidores deben actualizarse y pasar por el pipeline antes de llegar a producción. **Garantizar ese orden es parte del desafío.**

---

## Lo que Debes Entregar

El código fuente de los cinco módulos te es entregado funcionando. Tu trabajo es construir la **infraestructura de entrega** que los lleve de un commit hasta un ambiente listo para producción de forma automatizada, trazable y sin intervención manual.

Estimamos que esta actividad requiere entre **8 y 12 horas** de trabajo efectivo. No esperamos perfección — esperamos decisiones justificadas, pipelines que realmente corran y documentación que permita a otro ingeniero continuar donde lo dejaste.

---

### Entregable 1 — Repositorio y Estrategia de Versionamiento

Crea un repositorio (GitHub, GitLab u otro de tu elección) y organiza los cinco módulos. La estructura es decisión tuya: monorepo, multi-repo o híbrido.

Define y documenta una estrategia de branching y versionamiento que contemple:

- Versiones de las bibliotecas (`misc-utils`, `calc-lib`) independientes de las versiones de los servicios
- Separación entre rama de desarrollo y rama principal (producción)
- Cómo un cambio en una biblioteca desencadena la actualización de sus consumidores

---

### Entregable 2 — CI para Bibliotecas (`misc-utils` y `calc-lib`)

Configura un pipeline de CI para cada biblioteca que, ante cada cambio en la rama principal, ejecute:

1. Compilación con Maven (`mvn compile`)
2. Ejecución de todos los tests (`mvn test`)
3. Empaquetado del JAR (`mvn package`)

> **Sobre el repositorio de artefactos:** no es requisito configurar Nexus ni un registry externo. Es suficiente con que el JAR quede disponible como artefacto del pipeline (artifact upload en GitHub Actions, GitLab CI artifacts, o equivalente) y que los pipelines de los servicios lo descarguen desde ahí. Si prefieres usar GitHub Packages, GitLab Package Registry u otro registry real, también es válido — pero no es obligatorio.

El pipeline debe **bloquearse si algún test falla**. Ningún JAR que no pase todos los tests puede avanzar.

---

### Entregable 3 — CI para Servicios (`svc-misc`, `svc-calc`, `web-app`)

Configura un pipeline de CI para cada servicio que ejecute:

1. Resolución de dependencias (las bibliotecas internas deben venir del paso anterior, no de un `mvn install` manual en tu máquina)
2. Compilación y ejecución de tests de integración
3. Construcción de imagen Docker
4. Publicación de la imagen en un registry (Docker Hub, GHCR, GitLab Registry — el que prefieras)

La versión de la imagen debe ser **trazable al commit** que la originó (usa el SHA del commit, un tag semántico, o ambos). Evita etiquetar todo como `latest`.

---

### Entregable 4 — GitOps con ArgoCD para **un servicio**

Configura el despliegue continuo de **uno de los tres servicios** (el que prefieras) usando ArgoCD como motor declarativo.

Requisitos:

- Manifiestos de Kubernetes o Helm charts versionados en el repositorio
- ArgoCD sincroniza automáticamente los cambios con el cluster ante un merge a la rama principal
- Existe separación de configuración entre al menos **dos ambientes** (staging y production, o dev y staging)
- El despliegue en el ambiente de producción requiere **aprobación explícita** (manual gate)
- El rollback a la versión anterior debe ser posible sin necesidad de hacer un nuevo build

Para el cluster, usa el entorno que tengas disponible (Kind, k3s, minikube, cualquier cloud). Documenta cómo levantarlo.

---

### Entregable 5 — Documentación

Crea un archivo `SOLUTION.md` en la raíz del repositorio con:

1. **Diagrama o descripción de la arquitectura** de CI/CD que implementaste (ASCII, Mermaid o imagen)
2. **Justificación de las herramientas elegidas** para cada función (CI, distribución de artefactos, registry de imágenes, GitOps)
3. **Al menos un ADR** (Architecture Decision Record) sobre una decisión relevante — formato libre, lo importante es que quede claro el problema, las opciones consideradas y la elección
4. **Instrucciones para reproducir el ambiente** desde cero — otro ingeniero debe poder levantarlo sin preguntarte nada
5. **Qué agregarías en una segunda iteración** si tuvieras más tiempo

---

## Reglas

- **Sin credenciales hardcodeadas** en el repositorio. La forma en que las gestionas es decisión tuya — documéntala.
- Los pipelines deben haber corrido de verdad. No aceptamos pipelines escritos pero nunca ejecutados.
- El código fuente de las aplicaciones **no debe ser modificado** salvo que sea estrictamente necesario para que los pipelines funcionen. Si modificas algo, documenta el motivo.
- La herramienta de CI es libre: GitHub Actions, GitLab CI, Jenkins u otra. Justifica tu elección.

---

## Presentación

Después de los 5 días, agendaremos una sesión de **60 minutos**:

**Parte 1 — Walkthrough (aprox. 30 min)**
Comparte tu pantalla, muéstranos al menos un pipeline corriendo de punta a punta y recórrenos las decisiones que tomaste. Sin slides — queremos una conversación técnica.

**Parte 2 — Escenarios en vivo (aprox. 30 min)**
Te presentaremos situaciones hipotéticas sobre el sistema que construiste. No hay que implementar nada — es una conversación sobre cómo razonas y priorizas. El tipo de situaciones que exploraremos:

- La biblioteca `calc-lib` publicó una nueva versión. ¿Cómo garantiza tu pipeline que `svc-calc` y `web-app` usan la versión correcta antes de llegar a producción?
- ArgoCD muestra el servicio como `OutOfSync` pero nadie tocó los manifiestos. ¿Cómo lo investigas?
- Necesitas hacer rollback del servicio desplegado a la versión anterior. ¿Cómo lo ejecutas?
- Un pod entra en `CrashLoopBackOff` después de un deploy. ¿Cuáles son tus primeros pasos?
- Un desarrollador hizo push directo a `main` saltándose el pipeline. ¿Cómo lo previene de aquí en adelante?

No hay respuestas únicas correctas. Lo que nos interesa es tu razonamiento y cómo priorizas.

---

## Preguntas

Si tienes dudas sobre el enunciado, escribe a **[email de contacto]**. No respondemos preguntas sobre cómo resolver el problema técnico — esas decisiones son tuyas — pero sí aclaramos cualquier ambigüedad del enunciado.

---

*Esperamos ver cómo piensas, no solo qué construiste.*

**VMETRIX · Equipo de Ingeniería**
