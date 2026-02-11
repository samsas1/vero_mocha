# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:25-jdk AS builder
SHELL ["/bin/sh", "-c"]
WORKDIR /app

COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY gradle gradle
COPY settings.gradle build.gradle ./

RUN chmod +x gradlew
RUN ./gradlew --no-daemon :admin-api:dependencies :commons:dependencies || true

COPY . .

RUN ./gradlew --no-daemon :admin-api:bootJar

FROM eclipse-temurin:25-jre
ARG PORT=5001
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

ENV JAVA_OPTS="\
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75 \
  -XX:InitialRAMPercentage=50 \
  -XX:+ExitOnOutOfMemoryError \
"

COPY --from=builder /app/admin-api/build/libs/*.jar app.jar

EXPOSE ${PORT}

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

