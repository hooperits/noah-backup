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

```bash
# Copy example configuration
cp demo-application/src/main/resources/application-example.properties application.properties

# Edit configuration (see Configuration section)
nano application.properties
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
