# =========================================================================
# STAGE 1: Build the Application
# =========================================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache dependencies first to ensure ultra-fast subsequent builds
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy raw source files and compile the production artifact package
COPY src ./src
RUN mvn clean package -DskipTests

# =========================================================================
# STAGE 2: Lightweight Runtime Environment
# =========================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the compiled executable binary from Stage 1 safely
COPY --from=builder /app/target/mel-0.0.1-SNAPSHOT.jar app.jar

# Expose the correct network routing communications gateway port (documentation only;
# actual bound port is controlled at runtime by the PORT env var, see application.yml)
EXPOSE 8081

# Optimize JVM flag settings for tight cloud environments (prevents RAM leaks)
ENTRYPOINT ["sh", "-c", "java -XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Dspring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI} -jar app.jar --server.port=${PORT:-8081}"]