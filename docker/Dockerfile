# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app

# Install unzip for extraction
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

# Copy JAR and JPlag
COPY --from=build /app/target/*.jar app.jar
COPY jplag/jplag.jar ./jplag/

# Create directories
RUN mkdir -p /app/storage/uploads /app/storage/extracted /app/storage/reports /app/logs

# Create non-root user
RUN useradd -m -u 1000 plagiarism && chown -R plagiarism:plagiarism /app
USER plagiarism

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]