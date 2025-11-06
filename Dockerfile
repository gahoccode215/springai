# Sử dụng JDK 21 (hoặc version bạn dùng)
FROM eclipse-temurin:21-jdk

# Tạo thư mục app
WORKDIR /app

# Copy file pom.xml và tải dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ project và build
COPY . .
RUN mvn clean package -DskipTests

# Expose cổng cho Render (Render tự set PORT)
EXPOSE 8080

# Run app
CMD ["java", "-jar", "target/spring-ai-demo-0.0.1-SNAPSHOT.jar"]
