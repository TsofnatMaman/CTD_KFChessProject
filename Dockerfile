# ---------- Stage 1: Build ----------
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app

# Optional: cleaner logs & headless JVM
ENV MAVEN_OPTS="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -Dorg.slf4j.simpleLogger.showDateTime=true -Djava.awt.headless=true"

# Copy only POMs first to leverage Docker layer caching
COPY pom.xml .
COPY common/pom.xml common/
COPY server/pom.xml server/

# Warm up dependency cache for the server-only profile (best with BuildKit)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -P server-only -DskipTests dependency:go-offline

# Now copy sources
COPY common/src common/src
COPY server/src server/src

# Build from the root using the server-only profile (builds common+server; excludes client)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -P server-only -DskipTests clean package

# Normalize artifact name so the runtime stage can copy a fixed path
# Works for either *-shaded.jar or a regular *.jar
RUN set -eux; \
    JAR="$(ls server/target/*-shaded.jar 2>/dev/null || ls server/target/*.jar | head -n1)"; \
    test -n "$JAR"; \
    cp "$JAR" /app/app.jar


# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Run as non-root
RUN useradd --system --create-home --uid 10001 appuser
USER appuser

# Copy the built jar
COPY --from=build /app/app.jar ./app.jar

# Informational; app must read $PORT at runtime (your code already does via ServerConfig)
EXPOSE 8080

# JVM flags can be injected at runtime (e.g., on Render)
ENV JAVA_OPTS=""

# Use exec so the JVM is PID 1 (proper signal handling)
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar"]
