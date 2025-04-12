# Stage 1: Build với Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml riêng để tận dụng cache của Docker
COPY pom.xml .
# Tải dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build project
RUN mvn clean package -DskipTests

# Stage 2: Runtime với JRE
FROM eclipse-temurin:21-jre-jammy

# Tạo non-root user
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser

# Set working directory
WORKDIR /app

# Copy JAR từ build stage
COPY --from=build /app/target/*.jar RideConnectApplication-0.0.1-SNAPSHOT.jar

# Chown tất cả files cho non-root user
RUN chown -R javauser:javauser /app

# Switch to non-root user
USER javauser

# Cấu hình JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Port exposure
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar RideConnectApplication-0.0.1-SNAPSHOT.jar"]