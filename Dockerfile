# ---------- build stage ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /workspace

# copy pom first to leverage Docker cache for dependencies
COPY pom.xml .
# if you use a settings.xml, uncomment and copy it:
# COPY .mvn/settings.xml /root/.m2/settings.xml

# download dependencies (optional but speeds repeated builds)
RUN mvn -B dependency:go-offline -s /root/.m2/settings.xml || true

# copy source and build
COPY src ./src
# if you use application-specific profiles, change package command accordingly
RUN mvn -B -DskipTests package

# find the produced fat jar (Spring Boot)
RUN ls -lah target || true

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre
ARG JAR_FILE
WORKDIR /app

# copy jar from builder; fallback to any jar in target
COPY --from=builder /workspace/target/*.jar app.jar

# non-root user (optional)
RUN useradd -m -u 1000 appuser && chown -R appuser /app
USER appuser

# Let Render provide PORT env. Default to 8080 if not set.
ENV PORT=8080
EXPOSE 8080

# Spring Boot reads server.port; pass it from env
ENTRYPOINT ["sh","-c","exec java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
