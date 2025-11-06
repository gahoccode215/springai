# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy pom.xml trước để cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source code
COPY . .

# Build project (skip tests để nhanh)
RUN mvn clean package -DskipTests

# Stage 2: Runtime (Multi-stage build - giảm size)
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy JAR từ builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check (Render sẽ check)
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD java -cp app.jar org.springframework.boot.loader.JarLauncher || exit 1

# Run app (Render sẽ tự set PORT từ environment variable)
ENTRYPOINT ["sh", "-c", "java -Xmx512m -Xms256m -jar app.jar"]
