# syntax=docker/dockerfile:1.7
#
# Dockerfile único para os serviços Spring Boot (svc-calc, svc-misc, web-app).
#
# O JAR é construído e testado pelo Maven no pipeline ANTES do docker build;
# a imagem apenas empacota o artefato já validado (sem Maven nem código-fonte
# dentro da imagem, e sem precisar de credenciais do GitHub Packages no build).
#
# Build context: diretório do serviço.
#   docker build -f docker/Dockerfile --build-arg JAR_FILE=target/svc-calc-1.0.0.jar svc-calc

ARG JRE_IMAGE=eclipse-temurin:17-jre-jammy

FROM ${JRE_IMAGE} AS extract
WORKDIR /workspace
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
# Separa o fat-jar em camadas (dependências mudam pouco => melhor cache de layers).
RUN java -Djarmode=layertools -jar app.jar extract

FROM ${JRE_IMAGE}
RUN groupadd --system --gid 10001 app \
 && useradd --system --uid 10001 --gid app --home-dir /app --shell /usr/sbin/nologin app
WORKDIR /app

COPY --from=extract --chown=10001:10001 /workspace/dependencies/ ./
COPY --from=extract --chown=10001:10001 /workspace/spring-boot-loader/ ./
COPY --from=extract --chown=10001:10001 /workspace/snapshot-dependencies/ ./
COPY --from=extract --chown=10001:10001 /workspace/application/ ./

USER 10001
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
