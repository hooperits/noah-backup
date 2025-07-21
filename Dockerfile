# Noah Backup - Multi-stage Docker Build
# Production-ready container with security hardening

# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install build dependencies
RUN apk add --no-cache \
    curl \
    bash \
    git

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and dependencies for caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Copy all module build files
COPY filesystem-windows/build.gradle.kts filesystem-windows/
COPY storage-s3/build.gradle.kts storage-s3/
COPY scheduler-core/build.gradle.kts scheduler-core/
COPY auth-core/build.gradle.kts auth-core/
COPY rest-api/build.gradle.kts rest-api/
COPY report-core/build.gradle.kts report-core/
COPY appsec-core/build.gradle.kts appsec-core/
COPY demo-application/build.gradle.kts demo-application/

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY . .

# Build application
RUN ./gradlew clean build -x test --no-daemon && \
    ./gradlew bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Security: Create non-root user
RUN addgroup -g 1001 noahbackup && \
    adduser -D -s /bin/sh -u 1001 -G noahbackup noahbackup

# Install runtime dependencies
RUN apk add --no-cache \
    curl \
    bash \
    tzdata \
    dumb-init \
    ca-certificates && \
    # Update CA certificates
    update-ca-certificates && \
    # Clean up
    rm -rf /var/cache/apk/*

# Set timezone
ENV TZ=UTC

# Create application directory
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder --chown=noahbackup:noahbackup \
    /app/demo-application/build/libs/demo-application-*.jar \
    /app/noah-backup.jar

# Copy configuration files
COPY --from=builder --chown=noahbackup:noahbackup \
    /app/*/src/main/resources/application*.properties \
    /app/config/

# Copy PowerShell scripts (if they exist)
COPY --from=builder --chown=noahbackup:noahbackup \
    /app/filesystem-windows/src/main/resources/*.ps1 \
    /app/scripts/ 2>/dev/null || true

# Create logs directory
RUN mkdir -p /app/logs /app/data /app/backups && \
    chown -R noahbackup:noahbackup /app

# Security hardening
RUN chmod 750 /app && \
    chmod 640 /app/noah-backup.jar && \
    chmod 755 /app/logs /app/data /app/backups

# Switch to non-root user
USER noahbackup:noahbackup

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Environment variables
ENV JAVA_OPTS="-Xms512m -Xmx2048m" \
    SPRING_PROFILES_ACTIVE="docker" \
    SERVER_PORT=8080 \
    MANAGEMENT_SERVER_PORT=8081

# Expose ports
EXPOSE 8080 8081

# Labels for metadata
LABEL maintainer="Noah Backup Team" \
      version="1.0.0" \
      description="Noah Backup - Enterprise Backup Solution" \
      org.opencontainers.image.title="Noah Backup" \
      org.opencontainers.image.description="Secure, scalable backup solution with enterprise features" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.vendor="Noah Backup" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.source="https://github.com/your-org/noah-backup"

# Use dumb-init to handle signals properly
ENTRYPOINT ["dumb-init", "--"]

# Start application
CMD ["sh", "-c", "exec java $JAVA_OPTS -jar noah-backup.jar"]