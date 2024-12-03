# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY gradle gradle
COPY gradlew gradlew.bat ./
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew
COPY src src

RUN ./gradlew bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]