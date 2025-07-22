# 🚀 Noah Backup - Enterprise Backup Solution

<img src="Noah-Backup.png" alt="Noah Backup Logo" width="50%">

![CI/CD Pipeline](https://img.shields.io/badge/CI%2FCD-Pipeline-brightgreen?style=flat-square&logo=github-actions)
![Security Scan](https://img.shields.io/badge/Security-Scan-blue?style=flat-square&logo=security)
[![codecov](https://codecov.io/gh/your-org/noah-backup/branch/main/graph/badge.svg)](https://codecov.io/gh/your-org/noah-backup)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Docker Pulls](https://img.shields.io/docker/pulls/noahbackup/noah-backup)](https://hub.docker.com/r/noahbackup/noah-backup)
[![GitHub release](https://img.shields.io/github/release/your-org/noah-backup.svg)](https://github.com/your-org/noah-backup/releases/)

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)  
- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [API Documentation](#-api-documentation)
- [Security](#-security)
- [Deployment](#-deployment)
- [Monitoring](#-monitoring)
- [Contributing](#-contributing)
- [Support](#-support)
- [License](#-license)

## 🌟 Overview

Noah Backup is a **production-ready, enterprise-grade backup solution** designed for modern cloud infrastructure. Built with security-first principles, it provides comprehensive backup capabilities with professional-grade features including JWT authentication, audit logging, rate limiting, and multi-cloud storage support.

### 🎯 Why Noah Backup?

- **🛡️ Security First**: OWASP-compliant with comprehensive input validation, audit logging, and threat detection
- **🏢 Enterprise Ready**: JWT authentication, RBAC, rate limiting, and compliance reporting
- **☁️ Cloud Native**: Kubernetes-ready with Docker support and multi-cloud storage compatibility
- **🔄 CI/CD Integrated**: Complete DevOps pipeline with automated security scanning and zero-downtime deployments
- **📊 Monitoring & Alerts**: Built-in reporting, notifications, and health monitoring
- **🚀 High Performance**: Asynchronous processing, efficient resource utilization, and scalable architecture

## ✨ Features

### Core Backup Features
- ✅ **Windows VSS Integration** - Volume Shadow Copy Service support for consistent backups
- ✅ **S3-Compatible Storage** - AWS S3, MinIO, Lightsail Object Storage support
- ✅ **Scheduled Backups** - Cron-based scheduling with daily and weekly options
- ✅ **Incremental Backups** - Efficient space utilization with delta backups
- ✅ **Compression & Encryption** - AES-256-GCM encryption with optional compression

### Enterprise Security
- ✅ **JWT Authentication** - Stateless authentication with configurable expiration
- ✅ **Role-Based Access Control** - Admin, User, and Viewer permission levels
- ✅ **Input Validation** - Comprehensive protection against injection attacks
- ✅ **Rate Limiting** - Multi-level DOS protection and abuse prevention
- ✅ **Audit Logging** - Complete security audit trail with compliance support
- ✅ **Secret Management** - Secure credential handling with encryption at rest

### Professional API
- ✅ **RESTful Design** - Clean, consistent API following REST principles
- ✅ **OpenAPI Documentation** - Comprehensive API docs with interactive examples
- ✅ **Health Monitoring** - Built-in health checks and status endpoints
- ✅ **Error Handling** - Structured error responses with helpful messages
- ✅ **CORS Support** - Configurable cross-origin resource sharing

### DevOps & Operations
- ✅ **Containerized Deployment** - Docker and Kubernetes ready
- ✅ **CI/CD Pipeline** - Automated testing, security scanning, and deployment
- ✅ **Monitoring Integration** - Prometheus metrics and Grafana dashboards
- ✅ **Notification System** - Slack, email, and webhook notifications
- ✅ **Configuration Management** - Environment-based configuration with validation

### Reporting & Analytics
- ✅ **Backup Reporting** - Detailed operation reports with statistics
- ✅ **Daily Summaries** - Automated daily backup summaries
- ✅ **Historical Analysis** - Backup trends and success rate analysis
- ✅ **Retention Management** - Configurable log and backup retention policies

## 🏗️ Architecture

Noah Backup follows a modular, microservices-inspired architecture with clear separation of concerns:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   REST API      │    │  Report Core    │    │   AppSec Core   │
│  (JWT Auth)     │    │ (Logging/Alerts)│    │ (Security/Audit)│
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Scheduler Core │    │   Auth Core     │    │  Storage S3     │
│ (Cron Jobs)     │    │ (Encryption)    │    │ (Multi-Cloud)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │ Filesystem Win  │
                    │ (VSS Snapshots) │
                    └─────────────────┘
```

### Module Overview

| Module | Purpose | Key Features |
|--------|---------|--------------|
| **rest-api** | HTTP API Layer | JWT auth, RBAC, health checks |
| **auth-core** | Security Foundation | Encryption, validation, secure coding |
| **appsec-core** | Security Hardening | Input validation, audit logging, rate limiting |
| **scheduler-core** | Job Management | Cron scheduling, backup automation |
| **filesystem-windows** | File Operations | VSS snapshots, Windows integration |
| **storage-s3** | Cloud Storage | S3-compatible storage with multipart uploads |
| **report-core** | Logging & Reporting | Audit trails, notifications, analytics |

## 🚀 Quick Start

### Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Gradle 8.8+** 
- **Docker** (optional, for containerized deployment)
- **Windows** (for VSS backup features)

### 1. Clone Repository

```bash
git clone https://github.com/your-org/noah-backup.git
cd noah-backup
```

### 2. Build Application

```bash
# Build all modules
./gradlew build

# Build executable JAR
./gradlew bootJar
```

### 3. Configure Application

#### Option A: Interactive Setup (Recommended)
```bash
# Run the setup wizard
./scripts/setup.sh

# This will guide you through:
# - Environment selection (dev/prod)
# - JWT secret generation
# - S3 storage configuration
# - Backup paths setup
# - Notification setup (optional)
```

#### Option B: Manual Configuration
```bash
# Copy environment template
cp .env.example .env

# Edit configuration
nano .env

# Validate configuration
./scripts/validate-config.sh
```

#### Option C: Docker Quick Start
```bash
# Use Docker environment
cp .env.docker .env

# Start with Docker Compose
docker-compose up -d
```

### 4. Run Application

```bash
# Run with development profile
java -jar demo-application/build/libs/demo-application-*.jar --spring.profiles.active=dev

# Or run with Docker
docker run -p 8080:8080 -v ./application.properties:/app/config/application.properties noahbackup/noah-backup:latest
```

### 5. Access Application

- **API Base URL**: http://localhost:8080/api/v1
- **Health Check**: http://localhost:8080/actuator/health
- **API Documentation**: http://localhost:8080/swagger-ui.html

### 6. Authenticate

```bash
# Login to get JWT token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123!"}'

# Use token for authenticated requests
curl -X GET http://localhost:8080/api/v1/backup/status \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📦 Installation

### System Requirements

**Minimum Requirements:**
- **Operating System**: Windows 10/11, Windows Server 2016+, Linux (Docker)
- **Java**: OpenJDK or Oracle JDK 17+
- **Memory**: 4 GB RAM
- **Storage**: 100 GB available space
- **Network**: Outbound HTTPS access to S3 endpoints

**Recommended Production:**
- **Operating System**: Windows Server 2019+, Ubuntu 20.04+, RHEL 8+
- **Java**: OpenJDK 17+ with G1 garbage collector
- **Memory**: 8+ GB RAM
- **Storage**: 500+ GB SSD
- **Network**: Dedicated network with high bandwidth

### Installation Methods

#### Method 1: JAR Installation (Recommended)

**1. Download Release:**
```bash
# Download latest release
wget https://github.com/your-org/noah-backup/releases/latest/download/noah-backup.jar

# Or build from source
git clone https://github.com/your-org/noah-backup.git
cd noah-backup
./gradlew clean build bootJar
```

**2. Create Installation Directory:**
```bash
# Linux/macOS
sudo mkdir -p /opt/noah-backup/{config,data,logs}
sudo useradd -r -s /bin/false -d /opt/noah-backup noah-backup
sudo chown -R noah-backup:noah-backup /opt/noah-backup

# Windows
New-Item -Path "C:\Program Files\Noah Backup" -ItemType Directory -Force
```

**3. Install Application:**
```bash
# Linux/macOS
sudo cp noah-backup.jar /opt/noah-backup/
sudo chmod 644 /opt/noah-backup/noah-backup.jar

# Windows
Copy-Item noah-backup.jar "C:\Program Files\Noah Backup\"
```

**4. Create Configuration:**
```bash
# Copy example configuration
cp demo-application/src/main/resources/application.yml application.yml
# Edit configuration (see Configuration section)
```

#### Method 2: Docker Installation

**1. Pull Docker Image:**
```bash
docker pull noahbackup/noah-backup:latest
```

**2. Create Configuration:**
```bash
mkdir -p ./config ./data ./logs
# Create configuration file in ./config/application.yml
```

**3. Run Container:**
```bash
docker run -d \
  --name noah-backup \
  -p 8080:8080 \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=production \
  -v ./config:/app/config:ro \
  -v ./data:/app/data \
  -v ./logs:/app/logs \
  --restart unless-stopped \
  noahbackup/noah-backup:latest
```

### Service Installation

#### Linux (systemd)

**Create Service File:**
```bash
sudo tee /etc/systemd/system/noah-backup.service > /dev/null <<EOF
[Unit]
Description=Noah Backup Service
Documentation=https://docs.noah-backup.com
After=network-online.target
Wants=network-online.target

[Service]
Type=exec
User=noah-backup
Group=noah-backup
ExecStart=/usr/bin/java -jar /opt/noah-backup/noah-backup.jar
WorkingDirectory=/opt/noah-backup
StandardOutput=journal
StandardError=journal
Restart=on-failure
RestartSec=5

# Security settings
NoNewPrivileges=yes
PrivateTmp=yes
ProtectHome=yes
ProtectSystem=strict
ReadWritePaths=/opt/noah-backup

[Install]
WantedBy=multi-user.target
EOF

# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable noah-backup
sudo systemctl start noah-backup
```

#### Windows Service

**Using NSSM (Non-Sucking Service Manager):**
```powershell
# Install NSSM
choco install nssm

# Create service
nssm install "Noah Backup" "java" "-jar `"C:\Program Files\Noah Backup\noah-backup.jar`""
nssm set "Noah Backup" AppDirectory "C:\Program Files\Noah Backup"
nssm set "Noah Backup" DisplayName "Noah Backup Service"
nssm set "Noah Backup" Start SERVICE_AUTO_START
nssm start "Noah Backup"
```

### Verification

**1. Check Service Status:**
```bash
# Linux
sudo systemctl status noah-backup

# Windows
Get-Service "Noah Backup"

# Docker
docker ps | grep noah-backup
```

**2. Test Application:**
```bash
# Health check
curl http://localhost:8080/actuator/health

# API status
curl http://localhost:8080/api/v1/auth/status
```

### Post-Installation

**1. Initial Configuration:**
- Configure S3 storage credentials
- Set up backup paths
- Configure notification channels
- Set JWT secret for production

**2. Security Setup:**
- Change default passwords
- Configure SSL certificates
- Set up firewall rules
- Enable audit logging

**3. Monitoring Setup:**
- Configure health checks
- Set up log rotation
- Configure alerting
- Set up backup verification

## ⚙️ Configuration

Noah Backup provides multiple ways to configure your backup system, from simple guided setup to advanced customization. This section covers everything you need to get started quickly and securely.

### 🚀 Quick Configuration Setup

#### Option 1: Interactive Setup (Recommended)
```bash
# Run the interactive setup script
./scripts/setup.sh

# Validate your configuration
./scripts/validate-config.sh
```

#### Option 2: Manual Configuration
```bash
# Copy environment template
cp .env.example .env

# Edit configuration
nano .env

# Validate configuration
./scripts/validate-config.sh --test-s3
```

#### Option 3: Docker Quick Start
```bash
# Use Docker with pre-configured environment
cp .env.docker .env

# Start with Docker Compose
docker-compose up -d

# Check service health
docker-compose ps
```

### 📋 Configuration Overview

Noah Backup uses a layered configuration approach with the following priority order:

1. **Environment Variables** (highest priority)
2. **Command Line Arguments**
3. **Application Properties Files**
4. **Default Values** (lowest priority)

### 🔧 Configuration Files

Noah Backup includes several configuration files to make setup easier:

| File | Purpose | Use Case |
|------|---------|----------|
| `.env.example` | Complete template with all variables | Production setup, reference |
| `.env.docker` | Docker-specific configuration | Container deployment |
| `scripts/setup.sh` | Interactive configuration wizard | First-time setup |
| `scripts/validate-config.sh` | Configuration validator | Troubleshooting, CI/CD |

### 🔐 Environment Variables

All sensitive configuration should be managed through environment variables. Here are the essential variables:

#### Required Variables
```bash
# S3 Storage (Required)
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_REGION=us-east-1
S3_BUCKET_NAME=noah-backup-storage

# JWT Authentication (Required)
JWT_SECRET=your-super-secure-jwt-secret-min-32-chars
```

#### Essential Configuration
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

#### Optional Configuration
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

### 📚 Configuration Examples

#### AWS S3 Configuration
```bash
# Standard AWS S3
AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
AWS_REGION=us-west-2
S3_BUCKET_NAME=my-company-backups
# S3_ENDPOINT= (leave empty for AWS)
S3_PATH_STYLE_ACCESS=false
```

#### MinIO Configuration
```bash
# Self-hosted MinIO
AWS_ACCESS_KEY_ID=minioadmin
AWS_SECRET_ACCESS_KEY=minioadmin123
AWS_REGION=us-east-1
S3_BUCKET_NAME=noah-backups
S3_ENDPOINT=http://localhost:9000
S3_PATH_STYLE_ACCESS=true
```

#### Lightsail Object Storage
```bash
# AWS Lightsail Object Storage
AWS_ACCESS_KEY_ID=your-lightsail-key
AWS_SECRET_ACCESS_KEY=your-lightsail-secret
AWS_REGION=us-east-1
S3_BUCKET_NAME=noah-lightsail-backups
S3_ENDPOINT=https://storage.us-east-1.amazonaws.com
S3_PATH_STYLE_ACCESS=false
```

### 🔍 Configuration Validation

#### Using the Validation Script
```bash
# Basic validation
./scripts/validate-config.sh

# Validation with S3 connectivity test
./scripts/validate-config.sh --test-s3

# Get help
./scripts/validate-config.sh --help
```

#### Validation Checks
The validation script checks:
- ✅ Required environment variables are set
- ✅ JWT secret strength (minimum 32 characters)
- ✅ S3 configuration format and accessibility
- ✅ Backup paths exist and are accessible
- ✅ Cron expression format
- ✅ Notification configuration (if enabled)

#### Example Validation Output
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

### 🛠️ Troubleshooting Configuration

#### Common Issues and Solutions

| Issue | Symptom | Solution |
|-------|---------|----------|
| **JWT Secret Too Short** | Authentication errors | Generate new secret: `openssl rand -base64 64` |
| **S3 Access Denied** | Upload failures | Check AWS credentials and bucket permissions |
| **Invalid Backup Paths** | Backup failures | Verify paths exist and are accessible |
| **Port Already in Use** | Startup failure | Change `PORT` environment variable |
| **Invalid Cron Expression** | Scheduling issues | Validate cron format (6 parts for Spring) |

#### Debug Mode
```bash
# Enable debug logging
export LOG_LEVEL=DEBUG
export NOAH_LOG_LEVEL=DEBUG

# Start with detailed logging
./gradlew bootRun
```

#### Configuration Test Commands
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

### Core Configuration

#### Basic Application Settings

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

#### Backup Configuration

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

#### S3 Storage Configuration

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

### Security Configuration

#### Authentication & Authorization

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

#### Input Validation & Security

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

### Logging Configuration

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

### Notification Configuration

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

### Environment Variables

#### Required Variables

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

#### Optional Variables

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

### Profile-Specific Configuration

#### Development Profile

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

#### Production Profile

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

#### Docker Profile

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

### Configuration Validation

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

### Configuration Management

#### Using .env Files

Create a `.env` file for environment-specific settings:

```bash
# .env file
AWS_ACCESS_KEY_ID=AKIA...
AWS_SECRET_ACCESS_KEY=wJalr...
JWT_SECRET=your-super-secure-jwt-secret
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/...
EMAIL_SMTP_PASSWORD=email_password
```

#### Configuration File Locations

Noah Backup searches for configuration files in this order:

1. `./config/application.yml` (current directory)
2. `./application.yml` (current directory)
3. `classpath:/config/application.yml` (JAR resources)
4. `classpath:/application.yml` (JAR resources)

#### External Configuration

```bash
# Specify external configuration
java -jar noah-backup.jar --spring.config.location=file:/path/to/config/

# Multiple configuration files
java -jar noah-backup.jar \
  --spring.config.location=file:/etc/noah-backup/,file:/opt/noah-backup/config/
```

## 📖 API Documentation

### Overview

The Noah Backup REST API provides comprehensive programmatic access to all backup operations, system monitoring, and configuration management. The API follows RESTful principles with JWT-based authentication and role-based authorization.

**Base URL:** `http://localhost:8080/api/v1`

### Interactive Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html` (development only)
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`
- **Health Check**: `http://localhost:8080/actuator/health`

### Authentication

All API endpoints require JWT authentication (except health checks and authentication endpoints).

#### Getting Started

**1. Obtain JWT Token:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123!"
  }'
```

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "roles": ["ADMIN", "BACKUP_ADMIN"],
  "expiresIn": 86400000,
  "timestamp": "2024-01-15T10:30:00"
}
```

**2. Use Token in Requests:**
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/v1/backup/status
```

#### User Roles & Permissions

| Role | Permissions | API Access |
|------|-------------|------------|
| `BACKUP_ADMIN` | Full system access | All endpoints |
| `BACKUP_USER` | Backup operations, read config | Most endpoints |
| `BACKUP_VIEWER` | Read-only access | GET endpoints only |

### Core API Endpoints

#### Authentication Endpoints

**POST /api/v1/auth/login**
- **Purpose**: Authenticate user and receive JWT tokens
- **Access**: Public
- **Body**: `{"username": "string", "password": "string"}`

**POST /api/v1/auth/refresh**
- **Purpose**: Refresh an expired JWT token
- **Access**: Valid refresh token required
- **Headers**: `Authorization: Bearer <refresh_token>`

**GET /api/v1/auth/me**
- **Purpose**: Get current user information
- **Access**: Authenticated users
- **Returns**: User details and token status

**POST /api/v1/auth/logout**
- **Purpose**: Logout user (client should discard token)
- **Access**: Authenticated users
- **Returns**: Logout confirmation

#### Backup Operations

**POST /api/v1/backup/start**
- **Purpose**: Start manual backup operation
- **Access**: `BACKUP_ADMIN`, `BACKUP_USER`
- **Optional Body**: 
  ```json
  {
    "paths": ["C:\\Data", "C:\\Users"],
    "description": "Manual backup",
    "priority": "HIGH"
  }
  ```

**GET /api/v1/backup/status**
- **Purpose**: Get current backup system status
- **Access**: All authenticated users
- **Returns**: System status, configuration, and operational state

**POST /api/v1/backup/stop**
- **Purpose**: Stop running backup operation
- **Access**: `BACKUP_ADMIN`
- **Returns**: Stop operation result

**GET /api/v1/backup/history**
- **Purpose**: Get backup operation history
- **Access**: All authenticated users
- **Query Params**: `limit` (default: 10, max: 100)

**GET /api/v1/backup/health**
- **Purpose**: Detailed backup system health check
- **Access**: All authenticated users
- **Returns**: Component health status

#### Configuration Management

**GET /api/v1/config**
- **Purpose**: Get current backup configuration (sanitized)
- **Access**: All authenticated users
- **Returns**: Backup paths, schedules, and system settings

**GET /api/v1/config/system**
- **Purpose**: Get system-level configuration and capabilities
- **Access**: `BACKUP_ADMIN`
- **Returns**: System info, features, and limits

**POST /api/v1/config/schedule**
- **Purpose**: Update backup schedule configuration
- **Access**: `BACKUP_ADMIN`
- **Body**: 
  ```json
  {
    "dailySchedule": "0 3 * * *",
    "weeklySchedule": "0 4 * * 0",
    "enabled": true,
    "weeklyEnabled": true
  }
  ```

**GET /api/v1/config/validate**
- **Purpose**: Validate current configuration
- **Access**: `BACKUP_ADMIN`, `BACKUP_USER`
- **Returns**: Configuration validation results

#### Reporting Endpoints

**GET /api/v1/reports/daily/{date}**
- **Purpose**: Get backup reports for specific date
- **Access**: All authenticated users
- **Path Params**: `date` (YYYY-MM-DD format)

**GET /api/v1/reports/history**
- **Purpose**: Get recent backup operation history
- **Access**: All authenticated users
- **Query Params**: `days` (default: 7, min: 1, max: 90)

**GET /api/v1/reports/statistics**
- **Purpose**: Get backup statistics for date range
- **Access**: All authenticated users
- **Query Params**: `days` (default: 30, min: 1, max: 365)

**GET /api/v1/reports/available-dates**
- **Purpose**: Get list of dates with available reports
- **Access**: All authenticated users

**POST /api/v1/reports/notifications/test**
- **Purpose**: Send test notification to all channels
- **Access**: `BACKUP_ADMIN`

**POST /api/v1/reports/cleanup**
- **Purpose**: Trigger cleanup of old report files
- **Access**: `BACKUP_ADMIN`

### Rate Limiting

The API implements multi-level rate limiting to prevent abuse:

| Endpoint Type | Limit | Window |
|---------------|-------|---------|
| Authentication | 10 requests | 1 minute |
| Backup Operations | 50 requests | 1 hour |
| Admin Operations | 30 requests | 1 minute |
| General API | 60 requests | 1 minute |

**Rate Limit Headers:**
```http
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 45
X-RateLimit-Reset: 1705336860
```

### Error Responses

All endpoints return consistent error responses:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request", 
  "message": "Validation failed for field 'username'",
  "path": "/api/v1/auth/login"
}
```

**Common HTTP Status Codes:**
- `200 OK`: Request successful
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required/failed
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource conflict (e.g., backup running)
- `429 Too Many Requests`: Rate limit exceeded
- `500 Internal Server Error`: Server error

### API Examples

#### Complete Backup Workflow

```bash
# 1. Login and get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123!"}' | \
  jq -r '.token')

# 2. Check system status
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/backup/status

# 3. Validate configuration
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/config/validate

# 4. Start backup
curl -X POST http://localhost:8080/api/v1/backup/start \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"description":"API manual backup"}'

# 5. Check backup history
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/backup/history?limit=5"
```

#### Health Monitoring

```bash
# System health (no auth required)
curl http://localhost:8080/actuator/health

# Detailed backup health
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/backup/health

# System configuration
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/config/system
```

### SDKs and Tools

#### cURL Examples
All documentation examples use cURL for maximum compatibility.

#### Postman Collection
Download the complete Postman collection:
- [Noah Backup API Collection](postman/Noah_Backup_API.postman_collection.json)

#### HTTP Client Libraries

**JavaScript/Node.js:**
```javascript
const axios = require('axios');

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});

// Start backup
const response = await api.post('/backup/start', {
  description: 'Automated backup'
});
```

**Python:**
```python
import requests

headers = {
    'Authorization': f'Bearer {token}',
    'Content-Type': 'application/json'
}

# Get backup status
response = requests.get(
    'http://localhost:8080/api/v1/backup/status',
    headers=headers
)
```

### Webhooks

Noah Backup can send webhook notifications for backup events:

**Webhook Configuration:**
```json
{
  "url": "https://your-server.com/webhooks/noah-backup",
  "events": ["backup.started", "backup.completed", "backup.failed"],
  "secret": "your-webhook-secret",
  "headers": {
    "X-Custom-Header": "value"
  }
}
```

**Webhook Payload Example:**
```json
{
  "event": "backup.completed",
  "timestamp": "2024-01-15T10:30:00Z",
  "data": {
    "operationId": "backup-20240115-103000",
    "success": true,
    "filesProcessed": 1247,
    "duration": "00:15:33",
    "bucket": "noah-backup-prod",
    "size": "2.3 GB"
  },
  "signature": "sha256=..."
}
```

## 🔒 Security

### Security-First Architecture

Noah Backup implements defense-in-depth security with multiple layers of protection:

```
┌─────────────────────────────────────────────────────────┐
│                     Security Layers                     │
├─────────────────────────────────────────────────────────┤
│ Network     │ TLS 1.2+, Firewall Rules, IP Whitelisting │
│ Application │ JWT Auth, RBAC, Input Validation, CORS    │ 
│ Data        │ AES-256-GCM, Key Management, Encryption   │
│ Monitoring  │ Audit Logging, Threat Detection, Alerts   │
└─────────────────────────────────────────────────────────┘
```

### Authentication & Authorization

#### JWT Authentication
- **Algorithm**: HMAC-SHA256 signing
- **Token Expiration**: 24 hours (configurable)
- **Refresh Tokens**: 7 days (configurable)
- **Automatic Rotation**: Supported
- **Stateless**: No server-side session storage

#### Role-Based Access Control (RBAC)
- **BACKUP_ADMIN**: Full system access (create, read, update, delete)
- **BACKUP_USER**: Backup operations and configuration reading
- **BACKUP_VIEWER**: Read-only access to status and reports

#### Security Features
- BCrypt password hashing (strength 12)
- Password complexity requirements
- Account lockout protection
- Session timeout management

### Input Security & Validation

#### Comprehensive Threat Protection
Noah Backup protects against all major injection attacks:

- **SQL Injection**: Pattern detection, parameterized queries
- **XSS (Cross-Site Scripting)**: HTML encoding, CSP headers
- **Command Injection**: System command filtering
- **Path Traversal**: Directory traversal protection
- **LDAP Injection**: Metacharacter filtering
- **NoSQL Injection**: Query validation

#### Context-Aware Validation
Different input types receive appropriate validation:
- Email addresses
- File paths
- URLs and endpoints
- JSON payloads
- Query parameters

#### Real-Time Threat Detection
- **Critical**: Command injection, XXE → Block + Alert
- **High**: SQL injection, XSS → Block + Log
- **Medium**: Weak passwords → Log + Monitor
- **Low**: Format validation → Log only

### Rate Limiting & DDoS Protection

#### Multi-Level Rate Limiting
- **IP-Based**: 60 requests/minute, 1000/hour
- **User-Based**: Higher limits for authenticated users
- **Endpoint-Specific**: Custom limits per endpoint type

#### Advanced Protection
- Progressive penalties for repeat offenders
- IP whitelisting for trusted networks
- Automatic client blocking
- Resource usage monitoring

### Data Protection

#### Encryption
- **At Rest**: AES-256-GCM with AEAD
- **In Transit**: TLS 1.2+ with strong cipher suites
- **Key Management**: Secure key derivation and rotation
- **Secret Storage**: Environment variables, encrypted configs

#### Security Headers
```http
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
Content-Security-Policy: default-src 'self'
```

### Audit Logging & Monitoring

#### Comprehensive Audit Trail
All security events are logged with structured data:
- Authentication attempts (success/failure)
- Authorization decisions
- Security threats detected
- Configuration changes
- Administrative actions
- Data access events

#### Compliance Support
- **ISO 27001**: 3-year retention, all security events
- **SOC 2**: Access control and data handling
- **PCI DSS**: Authentication and data access
- **GDPR**: Data processing events

#### Real-Time Monitoring
- Automated threat detection
- Failed authentication alerts
- Unusual access pattern detection
- Configuration tampering alerts

### Security Configuration

#### Production Security Checklist
- [ ] Enable HTTPS with valid certificates
- [ ] Configure strong JWT secrets (256+ bits)
- [ ] Enable comprehensive audit logging
- [ ] Set appropriate rate limits
- [ ] Configure CORS for specific origins
- [ ] Enable all security headers
- [ ] Set up monitoring and alerting
- [ ] Configure backup encryption
- [ ] Implement proper secret management
- [ ] Set up log retention policies

#### Environment-Specific Settings

**Production:**
```yaml
noah:
  security:
    strict-mode: true
    audit:
      enabled: true
      retention-days: 1095  # 3 years
    ratelimit:
      enabled: true
      aggressive-threshold: 3
    monitoring:
      enabled: true
      alert-channels: slack,email,siem
```

**Development:**
```yaml
noah:
  security:
    strict-mode: false
    audit:
      enabled: true
    ratelimit:
      enabled: false
    validation:
      block-on-threat: false
```

### Security Testing

#### Automated Security Testing
- **Unit Tests**: Input validation, encryption, auth logic
- **Integration Tests**: End-to-end security flows
- **Regression Tests**: OWASP Top 10 test cases
- **Penetration Testing**: Quarterly security assessments

#### Vulnerability Management
- **Daily Scans**: Dependency and container vulnerabilities
- **CI/CD Integration**: SAST, DAST, dependency checking
- **Response SLAs**: Critical (24h), High (7d), Medium (30d)

### Incident Response

#### Security Incident Process
1. **Detection**: Automated monitoring alerts
2. **Analysis**: Security team assessment
3. **Containment**: Isolate affected systems
4. **Eradication**: Remove threats and fix vulnerabilities
5. **Recovery**: Restore normal operations
6. **Lessons Learned**: Update security controls

#### Emergency Contacts
- **Security Team**: security@noah-backup.com
- **24/7 Hotline**: +1-555-SECURITY
- **Incident Reports**: security-incidents@noah-backup.com

### Compliance Certifications

#### Supported Frameworks
- ✅ **ISO 27001**: Information Security Management
- ✅ **SOC 2 Type II**: Security, Availability, Integrity
- ✅ **PCI DSS**: Payment Card Industry Standards
- ✅ **GDPR**: General Data Protection Regulation

#### Security Best Practices
- Follow OWASP guidelines
- Implement least privilege access
- Regular security training
- Continuous vulnerability assessment
- Multi-factor authentication (where possible)

## 🚀 Deployment

### Deployment Overview

Noah Backup supports multiple deployment strategies from single-node installations to enterprise Kubernetes clusters.

| Method | Use Case | Complexity | Scalability |
|--------|----------|------------|-------------|
| **JAR File** | Development, small teams | Low | Single node |
| **Docker** | Containerized environments | Medium | Single/multi-node |
| **Docker Compose** | Small production | Medium | Multi-service |
| **Kubernetes** | Enterprise production | High | Highly scalable |

### Environment Requirements

#### Minimum Requirements
- **CPU**: 2 cores
- **Memory**: 4 GB RAM  
- **Storage**: 100 GB available space
- **Network**: Outbound HTTPS to S3 endpoints
- **OS**: Windows 10+, Linux (Docker)

#### Production Recommendations
- **CPU**: 4+ cores
- **Memory**: 8+ GB RAM
- **Storage**: 500+ GB SSD
- **Network**: Dedicated high-bandwidth connection
- **OS**: Windows Server 2019+, Ubuntu 20.04+

### JAR Deployment

#### Standard Installation

**1. Create Service User:**
```bash
# Linux
sudo useradd -r -s /bin/false -d /opt/noah-backup noah-backup
sudo mkdir -p /opt/noah-backup/{config,data,logs}
sudo chown -R noah-backup:noah-backup /opt/noah-backup

# Windows
New-Item -Path "C:\Program Files\Noah Backup" -ItemType Directory -Force
```

**2. Install Application:**
```bash
# Download and install
wget https://github.com/your-org/noah-backup/releases/latest/download/noah-backup.jar
sudo cp noah-backup.jar /opt/noah-backup/
sudo chmod 644 /opt/noah-backup/noah-backup.jar
```

**3. Create Systemd Service (Linux):**
```bash
sudo tee /etc/systemd/system/noah-backup.service > /dev/null <<EOF
[Unit]
Description=Noah Backup Service
After=network-online.target

[Service]
Type=exec
User=noah-backup
Group=noah-backup
ExecStart=/usr/bin/java -jar /opt/noah-backup/noah-backup.jar
WorkingDirectory=/opt/noah-backup
Restart=on-failure
RestartSec=5

# Security hardening
NoNewPrivileges=yes
PrivateTmp=yes
ProtectHome=yes
ProtectSystem=strict
ReadWritePaths=/opt/noah-backup

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable noah-backup
sudo systemctl start noah-backup
```

**4. Windows Service Installation:**
```powershell
# Using NSSM
nssm install "Noah Backup" "java" "-jar `"C:\Program Files\Noah Backup\noah-backup.jar`""
nssm set "Noah Backup" AppDirectory "C:\Program Files\Noah Backup"
nssm set "Noah Backup" DisplayName "Noah Backup Service"
nssm set "Noah Backup" Start SERVICE_AUTO_START
nssm start "Noah Backup"
```

### Docker Deployment

#### Single Container

```bash
# Create configuration directory
mkdir -p ./config ./data ./logs

# Run container
docker run -d \
  --name noah-backup \
  -p 8080:8080 \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID} \
  -e AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} \
  -e JWT_SECRET=${JWT_SECRET} \
  -v ./config:/app/config:ro \
  -v ./data:/app/data \
  -v ./logs:/app/logs \
  --restart unless-stopped \
  --memory 4g \
  --cpus 2 \
  --health-cmd="curl -f http://localhost:8080/actuator/health || exit 1" \
  --health-interval=30s \
  noahbackup/noah-backup:latest
```

#### Docker Compose

```yaml
version: '3.8'

services:
  noah-backup:
    image: noahbackup/noah-backup:latest
    container_name: noah-backup
    restart: unless-stopped
    ports:
      - "8080:8080"
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
      - AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
      - JWT_SECRET=${JWT_SECRET}
      - SLACK_WEBHOOK_URL=${SLACK_WEBHOOK_URL}
    volumes:
      - ./config:/app/config:ro
      - noah-backup-data:/app/data
      - noah-backup-logs:/app/logs
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
    deploy:
      resources:
        limits:
          memory: 4G
          cpus: '2.0'
        reservations:
          memory: 2G
          cpus: '1.0'

volumes:
  noah-backup-data:
  noah-backup-logs:
```

### Kubernetes Deployment

#### Basic Deployment

**namespace.yaml:**
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: noah-backup
  labels:
    name: noah-backup
    environment: production
```

**deployment.yaml:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: noah-backup
  namespace: noah-backup
spec:
  replicas: 2
  selector:
    matchLabels:
      app: noah-backup
  template:
    metadata:
      labels:
        app: noah-backup
    spec:
      containers:
      - name: noah-backup
        image: noahbackup/noah-backup:latest
        ports:
        - containerPort: 8080
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: AWS_ACCESS_KEY_ID
          valueFrom:
            secretKeyRef:
              name: noah-backup-secrets
              key: aws-access-key-id
        - name: AWS_SECRET_ACCESS_KEY
          valueFrom:
            secretKeyRef:
              name: noah-backup-secrets
              key: aws-secret-access-key
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: noah-backup-secrets
              key: jwt-secret
        resources:
          requests:
            memory: "2Gi"
            cpu: "500m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
```

**service.yaml:**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: noah-backup-service
  namespace: noah-backup
spec:
  selector:
    app: noah-backup
  ports:
  - name: http
    port: 8080
    targetPort: 8080
  - name: management
    port: 8081
    targetPort: 8081
  type: ClusterIP
```

**Deploy to Kubernetes:**
```bash
kubectl apply -f namespace.yaml
kubectl apply -f secrets.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

# Check deployment
kubectl get pods -n noah-backup
kubectl logs -f deployment/noah-backup -n noah-backup
```

### High Availability Setup

#### Load Balancer Configuration

**HAProxy Example:**
```haproxy
frontend noah-backup-frontend
    bind *:443 ssl crt /etc/ssl/certs/noah-backup.pem
    default_backend noah-backup-backend

backend noah-backup-backend
    balance roundrobin
    option httpchk GET /actuator/health
    server noah-backup-1 10.0.1.10:8080 check
    server noah-backup-2 10.0.1.11:8080 check
    server noah-backup-3 10.0.1.12:8080 check
```

#### Database Clustering

For production deployments with external databases:

```yaml
# PostgreSQL cluster example
apiVersion: postgresql.cnpg.io/v1
kind: Cluster
metadata:
  name: noah-backup-postgres
spec:
  instances: 3
  postgresql:
    parameters:
      max_connections: "200"
      shared_buffers: "256MB"
  storage:
    size: 100Gi
    storageClass: fast-ssd
```

### Environment-Specific Deployments

#### Development Environment
```bash
# Quick development setup
docker-compose -f docker-compose.dev.yml up -d

# Or run locally
export SPRING_PROFILES_ACTIVE=development
java -jar noah-backup.jar
```

#### Staging Environment
```bash
# Staging with production-like configuration
export SPRING_PROFILES_ACTIVE=staging
export AWS_ACCESS_KEY_ID=staging_key
export AWS_SECRET_ACCESS_KEY=staging_secret
export NOAH_BACKUP_S3_BUCKET=noah-backup-staging

docker-compose -f docker-compose.staging.yml up -d
```

#### Production Environment
```bash
# Production deployment
export SPRING_PROFILES_ACTIVE=production
export JWT_SECRET=production_jwt_secret_256_bits_minimum

# Deploy with Docker Compose
docker-compose -f docker-compose.prod.yml up -d

# Or deploy to Kubernetes
kubectl apply -f k8s/production/
```

### Monitoring & Health Checks

#### Health Check Endpoints
```bash
# Basic health
curl http://localhost:8080/actuator/health

# Detailed health
curl http://localhost:8081/actuator/health?showDetails=true

# Application-specific health
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/backup/health
```

#### Monitoring Setup
```yaml
# Prometheus monitoring
prometheus:
  enabled: true
  endpoints:
    - http://noah-backup:8081/actuator/prometheus
  
# Grafana dashboards
grafana:
  dashboards:
    - noah-backup-overview
    - noah-backup-security
    - noah-backup-performance
```

### Deployment Best Practices

#### Security
- Use non-root users in containers
- Enable TLS for all external connections
- Configure proper firewall rules
- Implement secrets management
- Enable audit logging

#### Performance
- Allocate sufficient memory (8GB+ for production)
- Use SSD storage for better I/O performance
- Configure appropriate JVM settings
- Enable compression for large backups
- Implement proper caching strategies

#### Reliability
- Configure health checks and readiness probes
- Implement proper backup and restore procedures
- Set up monitoring and alerting
- Use rolling deployments for zero downtime
- Configure automatic restart policies

## 📊 Monitoring

### Monitoring Overview

Noah Backup provides comprehensive monitoring capabilities through multiple channels including health checks, metrics collection, audit logging, and real-time alerting.

### Health Monitoring

#### Built-in Health Checks

**System Health Endpoint:**
```bash
# Basic health check (no authentication required)
curl http://localhost:8080/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

**Detailed Health Check:**
```bash
# Detailed health (requires authentication)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/actuator/health?showDetails=true
```

**Backup System Health:**
```bash
# Application-specific health
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/backup/health
```

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": "UP",
  "components": {
    "scheduler": "UP",
    "storage": "UP", 
    "configuration": "UP",
    "security": "UP"
  },
  "backup_paths": 3,
  "last_backup": "2024-01-15T02:00:00",
  "enabled": true
}
```

#### Custom Health Indicators

Noah Backup includes custom health indicators for:
- **Storage Connectivity**: S3 bucket access and permissions
- **Backup Scheduler**: Cron job status and next execution
- **Security System**: Authentication and authorization status
- **File System**: VSS service status and disk space
- **Configuration**: Validation of all required settings

### Metrics Collection

#### Prometheus Integration

**Metrics Endpoint:**
```bash
# Prometheus metrics
curl http://localhost:8081/actuator/prometheus
```

#### Key Metrics

**Backup Operations:**
- `backup_operations_total`: Total backup operations
- `backup_operations_successful_total`: Successful backups
- `backup_operations_failed_total`: Failed backups
- `backup_duration_seconds`: Backup operation duration
- `backup_files_processed_total`: Files processed in backups
- `backup_data_size_bytes`: Data size backed up

**Security Metrics:**
- `security_threats_detected_total`: Security threats detected
- `authentication_failures_total`: Failed authentication attempts
- `rate_limit_violations_total`: Rate limiting violations
- `security_audit_events_total`: Security events logged

**System Metrics:**
- `jvm_memory_used_bytes`: JVM memory usage
- `jvm_gc_pause_seconds`: Garbage collection pauses
- `system_cpu_usage`: System CPU utilization
- `disk_space_available_bytes`: Available disk space

**API Metrics:**
- `http_requests_total`: HTTP requests by endpoint
- `http_request_duration_seconds`: Request processing time
- `api_rate_limit_remaining`: Remaining rate limit quota

#### Prometheus Configuration

**prometheus.yml:**
```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'noah-backup'
    static_configs:
      - targets: ['noah-backup:8081']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s
    
  - job_name: 'noah-backup-kubernetes'
    kubernetes_sd_configs:
      - role: pod
        namespaces:
          names: [noah-backup]
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
```

### Grafana Dashboards

#### Main Dashboard Panels

**1. System Overview:**
- Application status and uptime
- Memory and CPU utilization
- Active backup operations
- System health status

**2. Backup Operations:**
- Backup success rate (%)
- Daily/weekly backup statistics
- Backup duration trends
- Failed backup alerts

**3. Security Monitoring:**
- Authentication success/failure rates
- Security threats detected
- Rate limiting violations
- Audit event trends

**4. Performance Metrics:**
- API response times
- Database query performance
- Storage I/O metrics
- Garbage collection impact

#### Dashboard Configuration

**Example Panel (Backup Success Rate):**
```json
{
  "title": "Backup Success Rate",
  "type": "stat",
  "targets": [
    {
      "expr": "(backup_operations_successful_total / backup_operations_total) * 100",
      "format": "time_series",
      "interval": "1h"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "unit": "percent",
      "min": 0,
      "max": 100,
      "thresholds": {
        "steps": [
          {"color": "red", "value": 0},
          {"color": "yellow", "value": 90},
          {"color": "green", "value": 95}
        ]
      }
    }
  }
}
```

### Alerting

#### Alert Rules

**Critical Alerts:**
```yaml
groups:
- name: noah-backup-critical
  rules:
  - alert: NoahBackupDown
    expr: up{job="noah-backup"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "Noah Backup service is down"
      description: "Noah Backup has been down for more than 1 minute"

  - alert: BackupFailure
    expr: increase(backup_operations_failed_total[5m]) > 0
    for: 0m
    labels:
      severity: critical
    annotations:
      summary: "Backup operation failed"
      description: "A backup operation has failed in the last 5 minutes"
```

**Warning Alerts:**
```yaml
  - alert: HighMemoryUsage
    expr: (jvm_memory_used_bytes / jvm_memory_max_bytes) * 100 > 80
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "High memory usage"
      description: "Memory usage above 80% for 5 minutes"

  - alert: SecurityThreatsDetected
    expr: increase(security_threats_detected_total[1h]) > 10
    for: 0m
    labels:
      severity: warning
    annotations:
      summary: "Multiple security threats detected"
      description: "More than 10 security threats in the last hour"
```

#### Notification Channels

**Slack Integration:**
```yaml
# alertmanager.yml
global:
  slack_api_url: 'https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK'

route:
  group_by: ['alertname']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 1h
  receiver: 'slack-notifications'

receivers:
- name: 'slack-notifications'
  slack_configs:
  - channel: '#noah-backup-alerts'
    title: 'Noah Backup Alert'
    text: '{{ range .Alerts }}{{ .Annotations.summary }}{{ end }}'
```

**Email Notifications:**
```yaml
receivers:
- name: 'email-notifications'
  email_configs:
  - to: 'admin@company.com'
    from: 'noah-backup@company.com'
    subject: 'Noah Backup Alert: {{ .GroupLabels.alertname }}'
    body: |
      Alert: {{ .GroupLabels.alertname }}
      
      {{ range .Alerts }}
      Summary: {{ .Annotations.summary }}
      Description: {{ .Annotations.description }}
      {{ end }}
```

### Log Monitoring

#### Structured Logging

Noah Backup uses structured JSON logging for better analysis:

```json
{
  "timestamp": "2024-01-15T10:30:00.000Z",
  "level": "INFO",
  "logger": "com.noahbackup.scheduler.BackupJobScheduler",
  "message": "Backup operation completed successfully",
  "context": {
    "operationId": "backup-20240115-103000",
    "duration": "00:15:33",
    "filesProcessed": 1247,
    "dataSize": "2.3 GB",
    "bucket": "noah-backup-prod"
  }
}
```

#### Log Aggregation

**ELK Stack Integration:**
```yaml
# logstash.conf
input {
  file {
    path => "/app/logs/noah-backup.log"
    codec => json
  }
}

filter {
  if [logger] == "com.noahbackup.security" {
    mutate {
      add_tag => ["security"]
    }
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "noah-backup-%{+YYYY.MM.dd}"
  }
}
```

**Fluentd Configuration:**
```yaml
<source>
  @type tail
  path /app/logs/noah-backup.log
  pos_file /var/log/fluentd/noah-backup.log.pos
  tag noah-backup
  format json
</source>

<match noah-backup>
  @type elasticsearch
  host elasticsearch
  port 9200
  index_name noah-backup
  type_name _doc
</match>
```

### Security Monitoring

#### Security Event Tracking

**Audit Log Monitoring:**
- Failed authentication attempts
- Authorization failures
- Security threats detected
- Configuration changes
- Administrative actions

**Real-time Alerts:**
```bash
# Monitor security events
tail -f /app/logs/audit.log | grep -i "SECURITY_THREAT"

# Check authentication failures
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/reports/security/failures?hours=1"
```

#### SIEM Integration

**Splunk Configuration:**
```conf
[monitor:///app/logs/noah-backup.log]
disabled = false
index = noah_backup
sourcetype = noah_backup_json

[noah_backup_json]
KV_MODE = json
TIME_PREFIX = timestamp
TIME_FORMAT = %Y-%m-%dT%H:%M:%S.%3N%Z
```

**Security Monitoring Queries:**
```sql
-- Failed authentication attempts
index=noah_backup sourcetype=noah_backup_json eventType=AUTHENTICATION outcome=FAILURE
| stats count by ipAddress, username
| where count > 5

-- Security threats by severity
index=noah_backup sourcetype=noah_backup_json eventType=SECURITY_THREAT
| stats count by severity, threatType
| sort -count
```

### Performance Monitoring

#### Application Performance Monitoring (APM)

**JVM Metrics:**
- Heap and non-heap memory usage
- Garbage collection frequency and duration
- Thread pool utilization
- Class loading statistics

**Application Metrics:**
- HTTP request processing times
- Database query performance
- File I/O operations
- Cache hit/miss ratios

#### Resource Monitoring

**System Resources:**
```bash
# CPU and memory monitoring
curl http://localhost:8081/actuator/metrics/system.cpu.usage
curl http://localhost:8081/actuator/metrics/jvm.memory.used

# Disk space monitoring  
curl http://localhost:8081/actuator/metrics/disk.free
curl http://localhost:8081/actuator/metrics/disk.total
```

**Storage Performance:**
```bash
# S3 operation metrics
curl http://localhost:8081/actuator/metrics/s3.upload.duration
curl http://localhost:8081/actuator/metrics/s3.upload.size

# Backup operation performance
curl http://localhost:8081/actuator/metrics/backup.duration
curl http://localhost:8081/actuator/metrics/backup.files.processed
```

### Monitoring Best Practices

#### Dashboard Design
- Use meaningful metrics and KPIs
- Implement proper alerting thresholds
- Include trend analysis and forecasting
- Provide drill-down capabilities
- Use color coding for status indication

#### Alert Management
- Avoid alert fatigue with proper thresholds
- Implement escalation procedures
- Group related alerts together
- Provide actionable information in alerts
- Regular review and tuning of alert rules

#### Data Retention
- Keep metrics data for trend analysis
- Archive logs based on compliance requirements
- Implement proper data lifecycle management
- Consider storage costs for long-term retention

## 🤝 Contributing

### Contributing Overview

We welcome contributions to Noah Backup! Whether you're fixing bugs, adding features, improving documentation, or helping with security, your contributions make the project better for everyone.

### Getting Started

#### Prerequisites

Before contributing, ensure you have:
- **Java 17+** (OpenJDK or Oracle JDK)
- **Gradle 8.8+**
- **Git** for version control
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code recommended)
- **Docker** (optional, for integration testing)

#### Development Setup

**1. Fork and Clone:**
```bash
# Fork the repository on GitHub, then clone your fork
git clone https://github.com/YOUR-USERNAME/noah-backup.git
cd noah-backup

# Add upstream remote
git remote add upstream https://github.com/your-org/noah-backup.git
```

**2. Development Environment:**
```bash
# Build the project
./gradlew clean build

# Run tests
./gradlew test

# Run the application
./gradlew bootRun
```

**3. IDE Setup:**
```bash
# Generate IDE project files
./gradlew idea      # IntelliJ IDEA
./gradlew eclipse   # Eclipse

# Import the project into your IDE
```

#### Development Workflow

**1. Create Feature Branch:**
```bash
# Update your local main branch
git checkout main
git pull upstream main

# Create and switch to feature branch
git checkout -b feature/your-feature-name
```

**2. Make Changes:**
- Write code following our coding standards
- Add tests for new functionality
- Update documentation as needed
- Ensure all tests pass

**3. Commit Changes:**
```bash
# Stage your changes
git add .

# Commit with descriptive message
git commit -m "feat: add backup validation API endpoint

- Add validation endpoint for backup configurations
- Include comprehensive error messages
- Add unit and integration tests
- Update API documentation"
```

**4. Push and Create PR:**
```bash
# Push to your fork
git push origin feature/your-feature-name

# Create Pull Request on GitHub
```

### Coding Standards

#### Java Code Style

**Formatting:**
- Use 4 spaces for indentation (no tabs)
- Line length limit: 120 characters
- Use Unix line endings (LF)
- No trailing whitespace

**Naming Conventions:**
- Classes: `PascalCase` (e.g., `BackupJobScheduler`)
- Methods/Variables: `camelCase` (e.g., `startBackupOperation`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `DEFAULT_TIMEOUT_MINUTES`)
- Packages: `lowercase.separated.by.dots`

**Best Practices:**
```java
// Good: Clear, descriptive method names
public BackupResult executeBackupOperation(List<String> paths, BackupConfiguration config) {
    // Implementation
}

// Good: Proper exception handling
try {
    backupService.performBackup(paths);
} catch (BackupException e) {
    logger.error("Backup operation failed: {}", e.getMessage(), e);
    throw new ServiceException("Backup operation failed", e);
}

// Good: Use Optional for nullable returns
public Optional<BackupResult> findLastBackup() {
    // Implementation
}
```

#### Security Guidelines

**Input Validation:**
```java
// Always validate input parameters
@PreAuthorize("hasRole('BACKUP_ADMIN')")
public ResponseEntity<BackupResponse> startBackup(
    @Valid @RequestBody BackupRequest request) {
    
    // Additional validation
    validationService.validateBackupPaths(request.getPaths());
    
    // Process request
}
```

**Error Handling:**
```java
// Don't expose sensitive information in error messages
catch (BackupException e) {
    // Log detailed error (with sensitive info)
    logger.error("Backup failed for user {}: {}", userId, e.getMessage(), e);
    
    // Return sanitized error to client
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("Backup operation failed"));
}
```

**Logging:**
```java
// Use structured logging with appropriate levels
logger.info("Backup operation started: operationId={}, paths={}, user={}", 
    operationId, pathCount, username);

// Don't log sensitive information
logger.debug("S3 configuration loaded: bucket={}, region={}", 
    config.getBucket(), config.getRegion());
// NOT: logger.debug("S3 config: {}", config); // May contain credentials
```

### Testing Standards

#### Test Structure

**Unit Tests:**
```java
@ExtendWith(MockitoExtension.class)
class BackupJobSchedulerTest {
    
    @Mock
    private BackupService backupService;
    
    @InjectMocks
    private BackupJobScheduler scheduler;
    
    @Test
    @DisplayName("Should schedule daily backup successfully")
    void shouldScheduleDailyBackup() {
        // Given
        BackupConfiguration config = createValidConfig();
        when(backupService.isEnabled()).thenReturn(true);
        
        // When
        ScheduleResult result = scheduler.scheduleDailyBackup(config);
        
        // Then
        assertThat(result.isSuccessful()).isTrue();
        verify(backupService).scheduleJob(any(JobDetail.class));
    }
}
```

**Integration Tests:**
```java
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class BackupIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldPerformCompleteBackupWorkflow() {
        // Authentication
        String token = authenticateAsAdmin();
        
        // Start backup
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        ResponseEntity<BackupResponse> response = restTemplate.exchange(
            "/api/v1/backup/start",
            HttpMethod.POST,
            new HttpEntity<>(createBackupRequest(), headers),
            BackupResponse.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

**Test Coverage:**
- Aim for 80%+ code coverage
- Focus on critical business logic
- Include negative test cases
- Test security controls
- Use meaningful test names

#### Security Testing

```java
@Test
void shouldRejectUnauthorizedBackupRequest() {
    // When: Request without authentication
    ResponseEntity<String> response = restTemplate.postForEntity(
        "/api/v1/backup/start", createBackupRequest(), String.class);
    
    // Then: Should return 401 Unauthorized
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
}

@Test
void shouldBlockSQLInjectionAttempt() {
    // Given: Malicious input
    String maliciousInput = "'; DROP TABLE users; --";
    
    // When: Validation is performed
    ValidationResult result = validator.validateInput(maliciousInput, ValidationContext.builder()
        .inputType(InputType.GENERAL_TEXT)
        .build());
    
    // Then: Should detect and block threat
    assertThat(result.isValid()).isFalse();
    assertThat(result.getThreatType()).isEqualTo(ThreatType.SQL_INJECTION);
}
```

### Documentation Standards

#### Code Documentation

**JavaDoc:**
```java
/**
 * Schedules and manages backup operations with support for daily and weekly schedules.
 * 
 * <p>This service handles the creation, scheduling, and monitoring of backup jobs
 * using Spring's task scheduling capabilities. It supports both cron-based and 
 * fixed-rate scheduling patterns.
 * 
 * @author Noah Backup Team
 * @since 1.0.0
 * @see BackupConfiguration
 * @see BackupService
 */
@Service
public class BackupJobScheduler {
    
    /**
     * Schedules a daily backup job based on the provided configuration.
     * 
     * @param config the backup configuration containing schedule details
     * @return the scheduling result with operation details
     * @throws SchedulingException if the job cannot be scheduled
     * @throws IllegalArgumentException if the configuration is invalid
     */
    public ScheduleResult scheduleDailyBackup(BackupConfiguration config) {
        // Implementation
    }
}
```

**API Documentation:**
```java
@RestController
@RequestMapping("/api/v1/backup")
@Tag(name = "Backup Operations", description = "Backup management and monitoring")
public class BackupController {
    
    @PostMapping("/start")
    @Operation(
        summary = "Start manual backup",
        description = "Initiates a manual backup operation for the specified paths",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Backup started successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "409", description = "Backup already running")
    })
    public ResponseEntity<BackupResponse> startBackup(
        @Parameter(description = "Backup request parameters")
        @Valid @RequestBody BackupRequest request) {
        // Implementation
    }
}
```

#### README and Guides

When updating documentation:
- Use clear, concise language
- Include practical examples
- Keep information current
- Follow Markdown standards
- Add table of contents for long documents

### Pull Request Process

#### PR Requirements

**Before Submitting:**
- [ ] All tests pass (`./gradlew test`)
- [ ] Code follows style guidelines
- [ ] Documentation is updated
- [ ] Security guidelines are followed
- [ ] No sensitive information in commits

**PR Description Template:**
```markdown
## Description
Brief description of changes made.

## Type of Change
- [ ] Bug fix (non-breaking change that fixes an issue)
- [ ] New feature (non-breaking change that adds functionality)
- [ ] Breaking change (fix or feature that causes existing functionality to change)
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed

## Security Considerations
- [ ] No sensitive data exposed
- [ ] Input validation implemented
- [ ] Authentication/authorization tested

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] Tests pass locally
```

#### Review Process

**Code Review Criteria:**
1. **Functionality**: Does the code work as intended?
2. **Security**: Are security best practices followed?
3. **Performance**: Are there any performance implications?
4. **Maintainability**: Is the code readable and maintainable?
5. **Testing**: Are adequate tests included?

**Review Timeline:**
- **Initial Review**: Within 2 business days
- **Follow-up Reviews**: Within 1 business day
- **Merge**: After approval from 2+ maintainers

### Bug Reports

#### Bug Report Template

```markdown
**Bug Description**
A clear description of the bug.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

**Expected Behavior**
What you expected to happen.

**Actual Behavior**
What actually happened.

**Environment**
- OS: [e.g., Windows 10, Ubuntu 20.04]
- Java Version: [e.g., OpenJDK 17.0.2]
- Noah Backup Version: [e.g., 1.0.0]
- Deployment Method: [e.g., JAR, Docker, Kubernetes]

**Logs**
```
Include relevant log snippets here
```

**Additional Context**
Any other information about the problem.
```

### Feature Requests

#### Feature Request Template

```markdown
**Feature Summary**
Brief description of the requested feature.

**Problem Statement**
Describe the problem this feature would solve.

**Proposed Solution**
Detailed description of the proposed feature.

**Alternatives Considered**
Other approaches you've considered.

**Additional Context**
Screenshots, mockups, or other supporting information.

**Implementation Notes**
Technical considerations or constraints.
```

### Community Guidelines

#### Code of Conduct

We are committed to providing a welcoming and inclusive environment:

- **Be Respectful**: Treat all community members with respect
- **Be Collaborative**: Work together towards common goals
- **Be Inclusive**: Welcome newcomers and diverse perspectives
- **Be Professional**: Maintain professional conduct in all interactions

#### Communication Channels

- **GitHub Issues**: Bug reports and feature requests
- **GitHub Discussions**: General questions and community discussion
- **Pull Requests**: Code contributions and reviews
- **Security Issues**: security@noah-backup.com (private)

#### Recognition

Contributors are recognized through:
- **Contributors file**: Listed in CONTRIBUTORS.md
- **Release Notes**: Major contributions mentioned
- **GitHub Badges**: Contributor status badges
- **Community Highlights**: Featured in project updates

### Release Process

#### Version Numbering

We follow Semantic Versioning (SemVer):
- **MAJOR**: Breaking changes (e.g., 1.0.0 → 2.0.0)
- **MINOR**: New features, backward compatible (e.g., 1.0.0 → 1.1.0)
- **PATCH**: Bug fixes, backward compatible (e.g., 1.0.0 → 1.0.1)

#### Release Workflow

**1. Pre-Release:**
- All tests pass
- Documentation updated
- Security review completed
- Performance testing done

**2. Release:**
- Version tag created
- Release notes generated
- Artifacts built and published
- Docker images updated

**3. Post-Release:**
- Community notification
- Documentation site updated
- Monitoring for issues

### Getting Help

#### Development Support

- **GitHub Discussions**: General development questions
- **Documentation**: Comprehensive guides and API docs
- **Examples**: Sample implementations and tutorials
- **Community**: Connect with other contributors

#### Mentorship

New contributors can request mentorship:
- **Getting Started**: Help with initial setup
- **Code Review**: Guidance on coding standards
- **Best Practices**: Learn project-specific practices
- **Career Development**: Open source contribution guidance

Thank you for contributing to Noah Backup! Your efforts help make enterprise backup solutions more secure and reliable for everyone.

## 📞 Support

### Getting Help

Noah Backup provides multiple support channels to help you successfully deploy, configure, and maintain your backup solution.

### Documentation Resources

#### Primary Documentation
- **Official Documentation**: [https://docs.noah-backup.com](https://docs.noah-backup.com)
- **API Reference**: [https://api-docs.noah-backup.com](https://api-docs.noah-backup.com)
- **Installation Guide**: [Installation Documentation](docs/installation.md)
- **Configuration Guide**: [Configuration Documentation](docs/configuration.md)
- **Security Guide**: [Security Documentation](docs/security.md)
- **Deployment Guide**: [Deployment Documentation](docs/deployment.md)

#### Quick References
- **Getting Started**: Complete setup in under 15 minutes
- **Common Configurations**: Pre-built configuration examples
- **Troubleshooting Guide**: Solutions for common issues
- **FAQ**: Frequently asked questions and answers
- **Best Practices**: Production deployment recommendations

### Community Support

#### GitHub Community
- **GitHub Issues**: [Report bugs and request features](https://github.com/your-org/noah-backup/issues)
- **GitHub Discussions**: [Community Q&A and general discussions](https://github.com/your-org/noah-backup/discussions)
- **GitHub Wiki**: [Community-contributed guides and examples](https://github.com/your-org/noah-backup/wiki)

#### Communication Channels
- **Slack Community**: [Join our Slack workspace](https://slack.noah-backup.com)
  - `#general`: General discussions
  - `#support`: Technical support questions
  - `#announcements`: Release and security announcements
  - `#development`: Development and contribution discussions

- **Discord Server**: [Join our Discord](https://discord.gg/noah-backup)
  - Real-time chat support
  - Voice channels for complex troubleshooting
  - Community events and office hours

#### Community Forums
- **Reddit Community**: [r/NoahBackup](https://reddit.com/r/noahbackup)
- **Stack Overflow**: Use tag `noah-backup` for questions
- **Hacker News**: Follow for updates and discussions

### Professional Support

#### Support Tiers

**Community Support (Free)**
- GitHub Issues and Discussions
- Community documentation
- Best-effort response from community
- Self-service troubleshooting guides

**Professional Support**
- **Response Time**: 2 business days
- **Coverage**: Business hours (9 AM - 5 PM EST)
- **Channels**: Email and ticketing system
- **Scope**: Installation, configuration, and usage questions
- **Price**: $199/month per organization

**Enterprise Support**
- **Response Time**: 4 hours for critical issues
- **Coverage**: 24/7 for production issues
- **Channels**: Email, phone, and dedicated Slack channel
- **Scope**: Full technical support including custom configurations
- **Extras**: Health checks, optimization reviews, priority feature requests
- **Price**: $999/month per organization

**Premium Support**
- **Response Time**: 1 hour for critical issues
- **Coverage**: 24/7 worldwide support
- **Channels**: All channels plus video calls and screen sharing
- **Scope**: Complete support including development assistance
- **Extras**: Dedicated support engineer, quarterly reviews, custom integrations
- **Price**: Custom pricing based on requirements

#### Enterprise Services

**Professional Services**
- **Implementation Services**: Full setup and configuration
- **Migration Services**: Migrate from existing backup solutions
- **Integration Services**: Custom integrations with existing systems
- **Training Services**: On-site or remote training for your team
- **Consultation Services**: Architecture review and optimization

**Managed Services**
- **Fully Managed**: Complete backup solution management
- **Monitoring & Alerting**: 24/7 monitoring with proactive alerts
- **Maintenance**: Regular updates, patching, and optimization
- **Compliance**: Compliance reporting and audit support

### Support Contacts

#### General Support
- **Email**: support@noah-backup.com
- **Response Time**: 2-4 business days
- **Hours**: Monday-Friday, 9 AM - 5 PM EST

#### Technical Support
- **Email**: technical@noah-backup.com
- **Phone**: +1-555-NOAH-TECH (+1-555-662-4832)
- **Hours**: Business hours for Professional/Enterprise customers

#### Security Issues
- **Email**: security@noah-backup.com
- **PGP Key**: [Download public key](https://noah-backup.com/pgp-key.asc)
- **Response Time**: 24 hours for security vulnerabilities
- **Severity Levels**:
  - **Critical**: 4 hours
  - **High**: 24 hours  
  - **Medium**: 72 hours
  - **Low**: 1 week

#### Emergency Support
- **24/7 Hotline**: +1-555-NOAH-911 (Enterprise+ customers only)
- **Emergency Email**: emergency@noah-backup.com
- **Escalation**: For production-down scenarios

#### Sales and Licensing
- **Sales Team**: sales@noah-backup.com
- **Phone**: +1-555-NOAH-SALE
- **Licensing**: licensing@noah-backup.com

### Self-Service Resources

#### Troubleshooting Tools

**Built-in Diagnostics:**
```bash
# Health check
curl http://localhost:8080/actuator/health

# Configuration validation
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/config/validate

# System information
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/config/system
```

**Log Analysis:**
```bash
# Check application logs
tail -f /app/logs/noah-backup.log

# Security events
grep -i "security" /app/logs/audit.log

# Error patterns
grep -i "error\|exception" /app/logs/noah-backup.log | tail -20
```

#### Common Issues and Solutions

**1. Application Won't Start**
```bash
# Check Java version
java -version

# Verify configuration
java -jar noah-backup.jar --spring.config.location=file:./application.yml --debug

# Check port availability
netstat -tulpn | grep :8080
```

**2. S3 Connection Issues**
```bash
# Test S3 connectivity
aws s3 ls s3://your-bucket-name

# Verify credentials
echo $AWS_ACCESS_KEY_ID
echo $AWS_SECRET_ACCESS_KEY

# Check network connectivity
curl -I https://s3.amazonaws.com
```

**3. Authentication Problems**
```bash
# Test authentication endpoint
curl -X POST http://localhost:8080/api/v1/auth/status

# Verify JWT configuration
grep -i jwt application.yml

# Check user credentials
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123!"}'
```

#### Knowledge Base

**Installation Issues**
- Java version compatibility
- Permission problems
- Service configuration
- Network connectivity

**Configuration Problems**
- S3 bucket setup
- Backup path configuration
- Scheduling issues
- Notification setup

**Performance Tuning**
- Memory optimization
- CPU utilization
- Storage performance
- Network throughput

**Security Configuration**
- JWT token setup
- SSL/TLS configuration
- Firewall rules
- User management

### Feature Requests

#### Submitting Feature Requests

**Process:**
1. Search existing feature requests
2. Use the feature request template
3. Provide detailed use case description
4. Include implementation suggestions
5. Gather community feedback

**Template:**
```markdown
**Feature Summary**
Brief description of the requested feature.

**Business Case**
Why is this feature needed? What problem does it solve?

**Proposed Solution**
How should this feature work?

**Acceptance Criteria**
What would make this feature complete?

**Priority**
High/Medium/Low and justification.
```

#### Feature Request Process

**1. Community Review** (1-2 weeks)
- Community discussion and feedback
- Use case validation
- Technical feasibility assessment

**2. Team Review** (2-4 weeks)  
- Product team evaluation
- Technical review and estimation
- Roadmap planning

**3. Implementation** (varies)
- Development planning
- Implementation and testing
- Documentation and release

#### Roadmap and Prioritization

**Release Planning**
- **Next Release**: Features in active development
- **Upcoming Releases**: Planned features for next 2-3 releases
- **Future**: Long-term vision and major features

**Prioritization Criteria**
- User impact and demand
- Security and compliance requirements
- Technical complexity and dependencies
- Resource availability
- Strategic alignment

### Bug Reporting

#### Bug Report Guidelines

**Before Reporting:**
- Search existing issues
- Verify with latest version
- Test in minimal environment
- Gather relevant information

**Required Information:**
- Clear bug description
- Steps to reproduce
- Expected vs actual behavior
- Environment details
- Log files and error messages
- Screenshots or videos (if applicable)

**Bug Severity Levels:**
- **Critical**: System down, data loss, security vulnerability
- **High**: Major functionality broken, significant impact
- **Medium**: Feature not working as expected, workaround available
- **Low**: Minor issue, cosmetic problem

#### Bug Response Times

| Severity | Response Time | Resolution Target |
|----------|---------------|-------------------|
| Critical | 2 hours | 24 hours |
| High | 8 hours | 72 hours |
| Medium | 2 business days | 2 weeks |
| Low | 1 week | Next minor release |

### Training and Education

#### Documentation and Guides
- **Administrator Guide**: Complete system administration
- **User Guide**: End-user functionality and workflows
- **Developer Guide**: API usage and integration
- **Security Guide**: Security best practices and configuration

#### Training Options

**Self-Paced Learning**
- **Online Documentation**: Comprehensive guides and tutorials
- **Video Tutorials**: Step-by-step implementation videos
- **Webinar Recordings**: Past training sessions and demos
- **Sample Configurations**: Real-world configuration examples

**Instructor-Led Training**
- **Virtual Training**: Remote training sessions
- **On-Site Training**: At your location (Enterprise customers)
- **Certification Programs**: Noah Backup Administrator certification
- **Custom Training**: Tailored to your specific environment

**Training Topics**
- Installation and initial setup
- Configuration and customization
- Security hardening
- Performance optimization
- Troubleshooting and maintenance
- API development and integration
- Monitoring and alerting

### Service Level Agreements (SLA)

#### Support SLA

**Response Times:**
- **Community**: Best effort, no guaranteed response time
- **Professional**: 2 business days for all issues
- **Enterprise**: 4 hours for critical, 8 hours for high, 1 business day for medium/low
- **Premium**: 1 hour for critical, 4 hours for high, 8 hours for medium, 1 business day for low

**Availability:**
- **Community**: No availability guarantee
- **Professional**: Business hours (9 AM - 5 PM EST, Monday-Friday)
- **Enterprise**: 24/7 for critical issues, business hours for others
- **Premium**: 24/7 for all issues

**Escalation:**
- **Level 1**: Front-line support technicians
- **Level 2**: Senior support engineers
- **Level 3**: Development team and architects
- **Emergency**: Direct access to on-call engineers (Enterprise+ only)

### Legal and Compliance

#### Privacy Policy
Your privacy is important to us. We collect minimal information necessary to provide support:
- Contact information for communication
- Technical details for troubleshooting
- Usage patterns for product improvement

#### Data Handling
- Support data is kept confidential
- Information shared only with relevant team members
- Data retention based on support tier and legal requirements
- Optional data sharing agreements for enhanced support

#### Terms of Service
- Support is provided under our Terms of Service
- Professional and Enterprise support includes additional SLAs
- Emergency support available for production-critical issues
- Custom support agreements available for enterprise customers

---

**Need immediate assistance?** 
- 🚨 **Emergency Support**: emergency@noah-backup.com
- 💬 **Live Chat**: [Available on our website](https://noah-backup.com/support)
- 📞 **Phone Support**: +1-555-NOAH-HELP

## 📄 License

### License Information

Noah Backup is released under the **MIT License**, providing maximum flexibility for both commercial and non-commercial use.

#### MIT License

```
MIT License

Copyright (c) 2025 HOOPERITS

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

### What This Means

#### ✅ Permissions
The MIT License grants you the following rights:

- **Commercial Use**: Use Noah Backup in commercial projects and environments
- **Modification**: Modify the source code to meet your specific requirements
- **Distribution**: Distribute copies of Noah Backup, both original and modified
- **Private Use**: Use Noah Backup in private projects and organizations
- **Sublicensing**: Include Noah Backup in larger software packages under different licenses

#### 📋 Conditions
When using Noah Backup, you must:

- **Include Copyright Notice**: Include the original copyright notice in all copies
- **Include License Text**: Include the full MIT license text in all distributions
- **Attribution**: Provide appropriate credit to the original authors

#### ❌ Limitations
The MIT License provides the software "as is" with:

- **No Warranty**: No guarantee that the software will work perfectly
- **No Liability**: Authors are not responsible for any damages from using the software
- **No Support Obligation**: No requirement to provide support or maintenance

### Third-Party Licenses

Noah Backup incorporates several open-source libraries and dependencies. Here are the key dependencies and their licenses:

#### Core Dependencies

**Spring Framework** (Apache License 2.0)
- Spring Boot
- Spring Security
- Spring Task Scheduling
- **License**: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- **Compatible**: ✅ Yes

**AWS SDK for Java** (Apache License 2.0)
- S3 Client Libraries
- **License**: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- **Compatible**: ✅ Yes

**SLF4J & Logback** (EPL 1.0 / LGPL 2.1)
- Logging Framework
- **License**: [Eclipse Public License 1.0](https://www.eclipse.org/legal/epl-v10.html)
- **Compatible**: ✅ Yes

**Jackson** (Apache License 2.0)
- JSON Processing
- **License**: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- **Compatible**: ✅ Yes

**JUnit** (Eclipse Public License 2.0)
- Testing Framework
- **License**: [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0/)
- **Compatible**: ✅ Yes (test scope)

#### Build and Development Tools

**Gradle** (Apache License 2.0)
- Build System
- **License**: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- **Compatible**: ✅ Yes

**Docker** (Apache License 2.0)
- Containerization Platform
- **License**: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- **Compatible**: ✅ Yes

#### License Compatibility

All included dependencies are compatible with the MIT License:

| Dependency | License | Compatibility |
|------------|---------|---------------|
| Spring Framework | Apache 2.0 | ✅ Compatible |
| AWS SDK | Apache 2.0 | ✅ Compatible |
| SLF4J/Logback | EPL 1.0/LGPL 2.1 | ✅ Compatible |
| Jackson | Apache 2.0 | ✅ Compatible |
| JUnit | EPL 2.0 | ✅ Compatible (test) |
| Gradle | Apache 2.0 | ✅ Compatible (build) |

### Commercial Use

#### Enterprise Usage
The MIT License explicitly permits commercial use of Noah Backup:

- **No Licensing Fees**: Use Noah Backup in commercial environments without licensing costs
- **No User Restrictions**: No limits on the number of users or installations
- **Revenue Generation**: You may charge for services built on top of Noah Backup
- **Modification Rights**: Modify the source code for commercial purposes
- **Distribution Rights**: Distribute Noah Backup as part of commercial solutions

#### Professional Services
While the software is free, professional services are available:

- **Support Contracts**: Professional and enterprise support available
- **Custom Development**: Paid development services for custom features
- **Training Services**: Commercial training and certification programs
- **Consulting**: Architecture review and optimization services

### Contributing and Copyright

#### Contributor License Agreement
Contributors retain copyright to their contributions while granting HOOPERITS the right to distribute the combined work under the MIT License.

#### Copyright Assignment
- **Original Code**: Copyright (c) 2025 HOOPERITS
- **Contributions**: Contributors retain copyright to their specific contributions
- **Combined Work**: Distributed under MIT License with appropriate attributions

#### Attribution Requirements
When distributing Noah Backup or derivative works:

```
This software includes Noah Backup
Copyright (c) 2025 HOOPERITS
Licensed under the MIT License
```

### Trademark Policy

#### Noah Backup Trademark
"Noah Backup" is a trademark of HOOPERITS. While you may use the software freely under the MIT License, trademark usage has restrictions:

**Permitted Uses:**
- Reference the software by name in documentation
- Indicate compatibility or integration with Noah Backup
- Use in academic or educational contexts

**Restricted Uses:**
- Cannot use "Noah Backup" in your product name without permission
- Cannot create confusingly similar product names
- Cannot imply endorsement or official relationship without agreement

#### Logo and Branding
The Noah Backup logo and visual branding are protected separately from the software license:

- **Logo Usage**: Requires separate permission for commercial use
- **Brand Guidelines**: Available at [https://noah-backup.com/brand](https://noah-backup.com/brand)
- **Attribution**: Proper attribution required when using logos

### License Compliance

#### Compliance Checklist
To ensure compliance with the MIT License:

- [ ] Include original copyright notice in all copies
- [ ] Include full MIT license text in distributions
- [ ] Provide appropriate attribution to original authors
- [ ] Do not remove or modify existing copyright notices
- [ ] Include license information in documentation

#### Distribution Requirements
When distributing Noah Backup:

**Source Code Distribution:**
- Include `LICENSE` file in root directory
- Maintain all existing copyright notices
- Include attribution in README or documentation

**Binary Distribution:**
- Include license text in About dialog or documentation
- Provide copyright notice in application credits
- Include third-party license information

**Modified Versions:**
- Clearly indicate modifications made
- Include original copyright and license
- Add your own copyright notice for modifications
- Document changes in changelog or release notes

### Legal Disclaimer

#### No Warranty
The MIT License provides the software "as is" without any warranty. This means:

- **No Guarantee of Functionality**: Software may not work as expected
- **No Support Obligation**: No requirement to provide support or fixes
- **Use at Your Own Risk**: Users assume all risks of using the software

#### Limitation of Liability
The authors and copyright holders are not liable for:

- **Direct Damages**: Any direct losses from using the software
- **Indirect Damages**: Consequential or incidental damages
- **Business Losses**: Lost profits or business interruption
- **Data Loss**: Loss or corruption of data

#### Legal Jurisdiction
This license is governed by the laws of the jurisdiction where HOOPERITS is incorporated.

### Frequently Asked Questions

#### Can I use Noah Backup in my commercial product?
**Yes.** The MIT License explicitly permits commercial use without restrictions.

#### Do I need to open source my modifications?
**No.** The MIT License does not require you to release your modifications or derivative works.

#### Can I rebrand Noah Backup as my own product?
**Partially.** You can modify and redistribute the software, but you must maintain copyright notices and cannot use the "Noah Backup" trademark without permission.

#### Do I need to pay licensing fees?
**No.** Noah Backup is completely free to use, modify, and distribute under the MIT License.

#### Can I get professional support?
**Yes.** While the software is free, professional support services are available for purchase.

#### What if I find a security vulnerability?
**Report it responsibly** to security@noah-backup.com. While there's no legal obligation to fix issues, we take security seriously and provide timely updates.

---

For additional legal questions or licensing clarification, contact: legal@noah-backup.com
