# Multi-stage build for Spring Boot + Vaadin application
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .

# Download dependencies (this layer is cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application with production profile (includes Vaadin frontend build)
RUN mvn clean package -Pproduction -DskipTests -B

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create upload directories WITH ROOT, then give to spring user
RUN mkdir -p /app/uploads/coverletters /app/uploads/resumes && \
    chown -R spring:spring /app/uploads

# Switch to non-root user
USER spring

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
