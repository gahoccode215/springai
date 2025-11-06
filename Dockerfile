FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . /app
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /usr/src/app

COPY --from=builder /app/target/spring-ai-0.0.1-SNAPSHOT.jar app.jar
COPY .env .env


COPY src/main/resources/MLN131_document.txt MLN131_document.txt

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]