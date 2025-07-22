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

For detailed installation instructions, see the **[Installation Guide](docs/installation.md)**.

## ⚙️ Configuration

For comprehensive configuration instructions, see the **[Configuration Guide](docs/configuration.md)**.

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

For comprehensive security information, see the **[Security Guide](docs/security.md)**.

## 🚀 Deployment

For detailed deployment instructions, see the **[Deployment Guide](docs/deployment.md)**.

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
- **Security Issues**: noah-backup@hooperits.com (private)

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


## 📞 Support

### Getting Help

Noah Backup provides multiple support channels to help you successfully deploy, configure, and maintain your backup solution.

### Documentation Resources

#### Primary Documentation
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
- **GitHub Issues**: [Report bugs and request features](https://github.com/hooperits/noah-backup/issues)
- **GitHub Discussions**: [Community Q&A and general discussions](https://github.com/hooperits/noah-backup/discussions)
- **GitHub Wiki**: [Community-contributed guides and examples](https://github.com/hooperits/noah-backup/wiki)

#### Support Tiers

**Community Support (Free)**
- GitHub Issues and Discussions
- Community documentation
- Best-effort response from community
- Self-service troubleshooting guides

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

#### Documentation and Guides
- **Administrator Guide**: Complete system administration
- **User Guide**: End-user functionality and workflows
- **Developer Guide**: API usage and integration
- **Security Guide**: Security best practices and configuration

### Service Level Agreements (SLA)

#### Support SLA

**Response Times:**
- **Community**: Best effort, no guaranteed response time

**Availability:**
- **Community**: No availability guarantee

### Legal and Compliance

#### Privacy Policy
Your privacy is important to us. We collect minimal information necessary to provide support:
- Contact information for communication
- Technical details for troubleshooting
- Usage patterns for product improvement

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
