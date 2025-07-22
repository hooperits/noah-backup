# Noah Backup - Configuration Guide

## 📋 Table of Contents

- [Quick Configuration Setup](#quick-configuration-setup)
- [Configuration Overview](#configuration-overview)
- [Configuration Files](#configuration-files)
- [Environment Variables](#environment-variables)
- [Configuration Examples](#configuration-examples)
- [Configuration Validation](#configuration-validation)
- [Troubleshooting Configuration](#troubleshooting-configuration)
- [Core Configuration](#core-configuration)
- [Security Configuration](#security-configuration)
- [Logging Configuration](#logging-configuration)
- [Notification Configuration](#notification-configuration)
- [Profile-Specific Configuration](#profile-specific-configuration)
- [Configuration Management](#configuration-management)

## 🚀 Quick Configuration Setup

### Option 1: Interactive Setup (Recommended)
```bash
# Run the interactive setup script
./scripts/setup.sh

# Validate your configuration
./scripts/validate-config.sh
```

### Option 2: Manual Configuration
```bash
# Copy environment template
cp .env.example .env

# Edit configuration
nano .env

# Validate configuration
./scripts/validate-config.sh --test-s3
```

### Option 3: Docker Quick Start
```bash
# Use Docker with pre-configured environment
cp .env.docker .env

# Start with Docker Compose
docker-compose up -d

# Check service health
docker-compose ps
```

## 📋 Configuration Overview

Noah Backup uses a layered configuration approach with the following priority order:

1. **Environment Variables** (highest priority)
2. **Command Line Arguments**
3. **Application Properties Files**
4. **Default Values** (lowest priority)

## 🔧 Configuration Files

Noah Backup includes several configuration files to make setup easier:

| File | Purpose | Use Case |
|------|---------|----------|
| `.env.example` | Complete template with all variables | Production setup, reference |
| `.env.docker` | Docker-specific configuration | Container deployment |
| `scripts/setup.sh` | Interactive configuration wizard | First-time setup |
| `scripts/validate-config.sh` | Configuration validator | Troubleshooting, CI/CD |

## 🔐 Environment Variables

All sensitive configuration should be managed through environment variables. Here are the essential variables:

### Required Variables
```bash
# S3 Storage (Required)
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_REGION=us-east-1
S3_BUCKET_NAME=noah-backup-storage

# JWT Authentication (Required)
JWT_SECRET=your-super-secure-jwt-secret-min-32-chars
```

### Essential Configuration
```bash
# Application Environment
NOAH_ENV=production                    # development, staging, production
SPRING_PROFILES_ACTIVE=production      # Spring profile to use
PORT=8080                             # Application port

# Backup Settings
NOAH_BACKUP_ENABLED=true
NOAH_BACKUP_PATHS=/path1;/path2       # Semicolon-separated paths
NOAH_SCHEDULE_DAILY=0 0 2 * * ?       # 2 AM daily
NOAH_SCHEDULE_WEEKLY=0 0 1 * * SUN    # 1 AM Sunday
```

### Optional Configuration
```bash
# Notifications
NOAH_NOTIFICATIONS_ENABLED=true
SMTP_HOST=smtp.gmail.com
SLACK_WEBHOOK_URL=https://hooks.slack.com/...

# Security
NOAH_SECURITY_CORS_ENABLED=false      # true for development
NOAH_API_DOCS_ENABLED=false          # true for development
NOAH_SECURITY_RATE_LIMITING_ENABLED=true

# Monitoring
LOG_LEVEL=INFO
NOAH_LOG_LEVEL=DEBUG
MANAGEMENT_ENDPOINTS_INCLUDE=health,info,metrics
```

## 📚 Configuration Examples

### AWS S3 Configuration
```bash
# Standard AWS S3
AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
AWS_REGION=us-west-2
S3_BUCKET_NAME=my-company-backups
# S3_ENDPOINT= (leave empty for AWS)
S3_PATH_STYLE_ACCESS=false
```

### MinIO Configuration
```bash
# Self-hosted MinIO
AWS_ACCESS_KEY_ID=minioadmin
AWS_SECRET_ACCESS_KEY=minioadmin123
AWS_REGION=us-east-1
S3_BUCKET_NAME=noah-backups
S3_ENDPOINT=http://localhost:9000
S3_PATH_STYLE_ACCESS=true
```

### Lightsail Object Storage
```bash
# AWS Lightsail Object Storage
AWS_ACCESS_KEY_ID=your-lightsail-key
AWS_SECRET_ACCESS_KEY=your-lightsail-secret
AWS_REGION=us-east-1
S3_BUCKET_NAME=noah-lightsail-backups
S3_ENDPOINT=https://storage.us-east-1.amazonaws.com
S3_PATH_STYLE_ACCESS=false
```

## 🔍 Configuration Validation

### Using the Validation Script
```bash
# Basic validation
./scripts/validate-config.sh

# Validation with S3 connectivity test
./scripts/validate-config.sh --test-s3

# Get help
./scripts/validate-config.sh --help
```

### Validation Checks
The validation script checks:
- ✅ Required environment variables are set
- ✅ JWT secret strength (minimum 32 characters)
- ✅ S3 configuration format and accessibility
- ✅ Backup paths exist and are accessible
- ✅ Cron expression format
- ✅ Notification configuration (if enabled)

### Example Validation Output
```bash
🔍 Noah Backup Configuration Validator
==============================================
✅ .env file found
🔐 Validating Required Variables
✅ AWS_ACCESS_KEY_ID
✅ AWS_SECRET_ACCESS_KEY
✅ AWS_REGION
✅ S3_BUCKET_NAME
✅ JWT_SECRET

🔑 Validating JWT Secret
✅ JWT_SECRET is properly configured

☁️  Validating S3 Configuration
✅ S3_BUCKET_NAME format is valid
✅ AWS_REGION format looks correct

📂 Validating Backup Paths
✅ C:\temp\test-backup
✅ Found 1 backup path(s)

🎉 Configuration validation passed!
🚀 Ready to start Noah Backup
```

## 🛠️ Troubleshooting Configuration

### Common Issues and Solutions

| Issue | Symptom | Solution |
|-------|---------|----------|
| **JWT Secret Too Short** | Authentication errors | Generate new secret: `openssl rand -base64 64` |
| **S3 Access Denied** | Upload failures | Check AWS credentials and bucket permissions |
| **Invalid Backup Paths** | Backup failures | Verify paths exist and are accessible |
| **Port Already in Use** | Startup failure | Change `PORT` environment variable |
| **Invalid Cron Expression** | Scheduling issues | Validate cron format (6 parts for Spring) |

### Debug Mode
```bash
# Enable debug logging
export LOG_LEVEL=DEBUG
export NOAH_LOG_LEVEL=DEBUG

# Start with detailed logging
./gradlew bootRun
```

### Configuration Test Commands
```bash
# Test S3 connectivity
aws s3 ls s3://your-bucket-name

# Test JWT secret strength
echo "your-jwt-secret" | wc -c  # Should be 32+

# Test cron expression
echo "0 0 2 * * ?" | grep -E '^[0-9*,/-]+ [0-9*,/-]+ [0-9*,/-]+ [0-9*,/-]+ [0-9*,/-]+ [0-9*,/-]+$'

# Check port availability
netstat -an | grep :8080
```

## Core Configuration

### Basic Application Settings

```yaml
# Server Configuration
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: noah-backup
  profiles:
    active: production

# Management endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

### Backup Configuration

```yaml
noah:
  backup:
    enabled: true
    weekly-enabled: true
    timeout-minutes: 60
    
    # Backup paths (Windows examples)
    paths:
      - "C:\\Users\\Administrator\\Documents"
      - "C:\\ProgramData\\ImportantApp"
      - "D:\\DatabaseBackups"
    
    # Scheduling (cron expressions)
    schedule:
      daily: "0 0 2 * * ?"    # 2:00 AM daily
      weekly: "0 0 1 * * SUN" # 1:00 AM Sunday
```

### S3 Storage Configuration

```yaml
# S3 Configuration
noah:
  storage:
    s3:
      bucket: noah-backup-prod
      region: us-east-1
      endpoint: https://s3.amazonaws.com  # Optional for S3-compatible storage
      access-key: ${AWS_ACCESS_KEY_ID}
      secret-key: ${AWS_SECRET_ACCESS_KEY}
      
      # Upload settings
      multipart-threshold: 100MB
      part-size: 50MB
      max-connections: 10
```

## Security Configuration

### Authentication & Authorization

```yaml
noah:
  api:
    jwt:
      secret: ${JWT_SECRET}  # Must be 256+ bits
      expiration: 86400000   # 24 hours
      refresh-expiration: 604800000  # 7 days
    
    security:
      enabled: true
      cors-enabled: false  # Production
      rate-limiting-enabled: true
      max-requests-per-hour: 1000
```

### Input Validation & Security

```yaml
noah:
  security:
    strict-mode: true
    audit:
      enabled: true
      include-request-details: true
      retention-days: 90
    
    ratelimit:
      enabled: true
      default:
        requests-per-minute: 60
      authentication:
        requests-per-minute: 10
      backup:
        requests-per-hour: 50
    
    validation:
      strict: true
      block-on-threat: true
      threat-threshold: 3
```

## Logging Configuration

```yaml
logging:
  level:
    com.noahbackup: INFO
    com.noahbackup.security: WARN
  
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  
  file:
    name: /app/logs/noah-backup.log
    max-size: 100MB
    max-history: 30
  
  logback:
    rollingpolicy:
      clean-history-on-start: true
```

## Notification Configuration

```yaml
noah:
  notifications:
    enabled: true
    channels: log,slack,email
    
    # Backup notifications
    backup:
      success:
        enabled: true
        channels: log,slack
      failure:
        enabled: true
        channels: log,slack,email
    
    # System alerts
    system:
      alerts:
        enabled: true
        channels: email
    
    # Email settings
    email:
      smtp:
        host: ${EMAIL_SMTP_HOST}
        port: 587
        username: ${EMAIL_SMTP_USERNAME}
        password: ${EMAIL_SMTP_PASSWORD}
        starttls: true
      from:
        address: noah-backup@company.com
        name: Noah Backup
      to:
        addresses: admin@company.com,backup-team@company.com
    
    # Slack settings
    slack:
      webhook-url: ${SLACK_WEBHOOK_URL}
      channel: "#backups"
      username: "Noah Backup"
      icon-emoji: ":floppy_disk:"
```

## Environment Variables

### Required Variables

```bash
# S3 Storage (Required)
export AWS_ACCESS_KEY_ID="AKIA..."
export AWS_SECRET_ACCESS_KEY="wJalr..."
export AWS_DEFAULT_REGION="us-east-1"
export NOAH_BACKUP_S3_BUCKET="noah-backup-prod"

# Security (Required for production)
export JWT_SECRET="your-super-secure-jwt-secret-key-256-bits-minimum"

# Application Profile
export SPRING_PROFILES_ACTIVE="production"
```

### Optional Variables

```bash
# Database (if using external database)
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/noah_backup"
export SPRING_DATASOURCE_USERNAME="noah_backup"
export SPRING_DATASOURCE_PASSWORD="secure_password"

# Notifications
export SLACK_WEBHOOK_URL="https://hooks.slack.com/services/..."
export EMAIL_SMTP_HOST="smtp.company.com"
export EMAIL_SMTP_USERNAME="noah-backup@company.com"
export EMAIL_SMTP_PASSWORD="email_password"

# SSL/TLS
export SERVER_SSL_ENABLED="true"
export SERVER_SSL_KEY_STORE="classpath:keystore.p12"
export SERVER_SSL_KEY_STORE_PASSWORD="keystore_password"

# Monitoring
export PROMETHEUS_ENABLED="true"
export GRAFANA_API_KEY="grafana_api_key"
```

## Profile-Specific Configuration

### Development Profile

```yaml
spring:
  config:
    activate:
      on-profile: development

logging:
  level:
    com.noahbackup: DEBUG
    org.springframework.security: DEBUG

noah:
  api:
    docs:
      enabled: true  # Enable Swagger UI
    security:
      cors-enabled: true
      rate-limiting-enabled: false
  
  backup:
    schedule:
      daily: "0 */30 * * * ?"  # Every 30 minutes for testing
```

### Production Profile

```yaml
spring:
  config:
    activate:
      on-profile: production

logging:
  level:
    com.noahbackup: INFO
    com.noahbackup.security: WARN

noah:
  api:
    docs:
      enabled: false  # Disable Swagger UI
    security:
      cors-enabled: false
      rate-limiting-enabled: true
  
  security:
    strict-mode: true
    audit:
      enabled: true
```

### Docker Profile

```yaml
spring:
  config:
    activate:
      on-profile: docker

logging:
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

# Docker-specific paths
noah:
  backup:
    paths:
      - "/app/data/backup-source"
  
  reports:
    directory: "/app/logs"
```

## Configuration Validation

Noah Backup includes built-in configuration validation accessible via the API:

```bash
# Validate current configuration
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/config/validate
```

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "valid": true,
  "checks": {
    "backup_paths": "OK: 3 paths configured",
    "s3_bucket": "OK: Bucket configured and accessible",
    "scheduling": "OK: Scheduling enabled",
    "security": "OK: JWT secret configured",
    "notifications": "OK: 2 channels configured"
  }
}
```

## Configuration Management

### Using .env Files

Create a `.env` file for environment-specific settings:

```bash
# .env file
AWS_ACCESS_KEY_ID=AKIA...
AWS_SECRET_ACCESS_KEY=wJalr...
JWT_SECRET=your-super-secure-jwt-secret
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/...
EMAIL_SMTP_PASSWORD=email_password
```

### Configuration File Locations

Noah Backup searches for configuration files in this order:

1. `./config/application.yml` (current directory)
2. `./application.yml` (current directory)
3. `classpath:/config/application.yml` (JAR resources)
4. `classpath:/application.yml` (JAR resources)

### External Configuration

```bash
# Specify external configuration
java -jar noah-backup.jar --spring.config.location=file:/path/to/config/

# Multiple configuration files
java -jar noah-backup.jar \
  --spring.config.location=file:/etc/noah-backup/,file:/opt/noah-backup/config/
```

---

For installation instructions, see the [Installation Guide](installation.md).
For security configuration, see the [Security Guide](security.md).