# 🚀 Noah Backup - Deployment Guide

## Overview

This guide covers production deployment of Noah Backup across different environments and platforms, from single-node installations to enterprise Kubernetes clusters.

## Deployment Options

| Method | Use Case | Complexity | Scalability |
|--------|----------|------------|-------------|
| **JAR File** | Development, small teams | Low | Single node |
| **Docker** | Containerized environments | Medium | Single/multi-node |
| **Docker Compose** | Small production deployments | Medium | Multi-service |
| **Kubernetes** | Enterprise production | High | Highly scalable |
| **Cloud Platforms** | Managed cloud deployment | Medium | Platform-managed |

## Prerequisites

### System Requirements

**Minimum Requirements:**
- **CPU**: 2 cores
- **Memory**: 4 GB RAM
- **Storage**: 100 GB available space
- **Network**: Outbound HTTPS access to S3 endpoints
- **OS**: Windows 10/11, Windows Server 2016+, Linux (Docker)

**Recommended Production:**
- **CPU**: 4+ cores
- **Memory**: 8+ GB RAM
- **Storage**: 500+ GB SSD
- **Network**: Dedicated network with high bandwidth
- **OS**: Windows Server 2019+, Ubuntu 20.04+, RHEL 8+

**High-Load Production:**
- **CPU**: 8+ cores
- **Memory**: 16+ GB RAM
- **Storage**: 1+ TB NVMe SSD
- **Network**: 10 Gbps+ network connectivity
- **Load Balancer**: For high availability setups

### Software Dependencies

**Required:**
- Java 17 or higher (OpenJDK or Oracle JDK)
- PowerShell 5.1+ (for VSS features on Windows)

**Optional:**
- Docker 20.10+ (for containerized deployment)
- Kubernetes 1.20+ (for orchestrated deployment)
- PostgreSQL 12+ (for external database)
- Redis 6.0+ (for distributed rate limiting)

## Environment Configuration

### Environment Variables

**Required Variables:**
```bash
# S3 Configuration
export NOAH_BACKUP_S3_BUCKET="noah-backup-prod"
export NOAH_BACKUP_S3_ACCESS_KEY="AKIA..."
export NOAH_BACKUP_S3_SECRET_KEY="wJalr..."
export NOAH_BACKUP_S3_REGION="us-east-1"

# Security
export JWT_SECRET="your-super-secure-jwt-secret-key-256-bits-minimum"

# Application Profile
export SPRING_PROFILES_ACTIVE="production"
```

**Optional Variables:**
```bash
# Database (if using external database)
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/noah_backup"
export SPRING_DATASOURCE_USERNAME="noah_backup"
export SPRING_DATASOURCE_PASSWORD="secure_password"

# Notification
export SLACK_WEBHOOK_URL="https://hooks.slack.com/services/..."
export EMAIL_SMTP_HOST="smtp.company.com"
export EMAIL_SMTP_USERNAME="noah-backup@company.com"
export EMAIL_SMTP_PASSWORD="email_password"

# Monitoring
export PROMETHEUS_ENABLED="true"
export GRAFANA_API_KEY="grafana_api_key"

# Security
export RATE_LIMIT_REDIS_URL="redis://localhost:6379"
export SECURITY_AUDIT_SIEM_ENDPOINT="https://siem.company.com/api/events"
```

### Configuration Files

Create production configuration file:

**application-production.properties:**
```properties
# Server Configuration
server.port=8080
server.shutdown=graceful
management.server.port=8081
management.endpoints.web.exposure.include=health,info,metrics,prometheus

# Logging
logging.level.com.noahbackup=INFO
logging.level.com.noahbackup.security=WARN
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
logging.file.name=/app/logs/noah-backup.log
logging.file.max-size=100MB
logging.file.max-history=30

# Security
noah.security.strict-mode=true
noah.security.audit.enabled=true
noah.security.ratelimit.enabled=true
noah.security.monitoring.enabled=true

# Backup Configuration
noah.backup.enabled=true
noah.backup.schedule.daily=0 2 * * *
noah.backup.schedule.weekly=0 3 * * 0
noah.backup.timeout.minutes=120
noah.backup.retry.max-attempts=3

# Notification
noah.notifications.enabled=true
noah.notifications.channels=log,slack,email
noah.notifications.backup.success.enabled=true
noah.notifications.backup.failure.enabled=true

# Performance Tuning
spring.task.execution.pool.core-size=4
spring.task.execution.pool.max-size=16
spring.task.scheduling.pool.size=4

# Health Checks
management.health.probes.enabled=true
management.endpoint.health.show-details=always
```

## JAR File Deployment

### Standard Installation

**1. Download Application:**
```bash
# Download latest release
wget https://github.com/your-org/noah-backup/releases/latest/download/noah-backup.jar

# Or build from source
git clone https://github.com/your-org/noah-backup.git
cd noah-backup
./gradlew clean build bootJar
```

**2. Create Application Directory:**
```bash
sudo mkdir -p /opt/noah-backup/{config,data,logs}
sudo chown -R noah-backup:noah-backup /opt/noah-backup
```

**3. Install Application:**
```bash
sudo cp noah-backup.jar /opt/noah-backup/
sudo cp application-production.properties /opt/noah-backup/config/
```

**4. Create Service User:**
```bash
sudo useradd -r -s /bin/false -d /opt/noah-backup noah-backup
sudo chown -R noah-backup:noah-backup /opt/noah-backup
```

**5. Create Systemd Service:**
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
ExecStart=/usr/bin/java -jar /opt/noah-backup/noah-backup.jar --spring.config.location=file:/opt/noah-backup/config/application-production.properties
WorkingDirectory=/opt/noah-backup
StandardOutput=journal
StandardError=journal
SyslogIdentifier=noah-backup
KillMode=mixed
KillSignal=SIGTERM
RestartKillSignal=SIGTERM
SendSIGKILL=no
SuccessExitStatus=0 143
Restart=on-failure
RestartSec=5

# Security settings
NoNewPrivileges=yes
PrivateTmp=yes
PrivateDevices=yes
ProtectHome=yes
ProtectSystem=strict
ReadWritePaths=/opt/noah-backup

# Resource limits
MemoryHigh=6G
MemoryMax=8G
TasksMax=1000

[Install]
WantedBy=multi-user.target
EOF
```

**6. Start and Enable Service:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable noah-backup
sudo systemctl start noah-backup
sudo systemctl status noah-backup
```

### Windows Installation

**1. Create Application Directory:**
```powershell
New-Item -Path "C:\Program Files\Noah Backup" -ItemType Directory -Force
New-Item -Path "C:\Program Files\Noah Backup\config" -ItemType Directory -Force
New-Item -Path "C:\Program Files\Noah Backup\data" -ItemType Directory -Force
New-Item -Path "C:\Program Files\Noah Backup\logs" -ItemType Directory -Force
```

**2. Install Application:**
```powershell
Copy-Item noah-backup.jar "C:\Program Files\Noah Backup\"
Copy-Item application-production.properties "C:\Program Files\Noah Backup\config\"
```

**3. Create Windows Service:**
```powershell
# Using NSSM (Non-Sucking Service Manager)
nssm install "Noah Backup" "java" "-jar `"C:\Program Files\Noah Backup\noah-backup.jar`" --spring.config.location=file:`"C:\Program Files\Noah Backup\config\application-production.properties`""
nssm set "Noah Backup" AppDirectory "C:\Program Files\Noah Backup"
nssm set "Noah Backup" DisplayName "Noah Backup Service"
nssm set "Noah Backup" Description "Enterprise Backup Solution with VSS Support"
nssm set "Noah Backup" Start SERVICE_AUTO_START
nssm start "Noah Backup"
```

## Docker Deployment

### Single Container

**1. Create Docker Network:**
```bash
docker network create noah-backup-network
```

**2. Prepare Configuration:**
```bash
mkdir -p ./config ./data ./logs
cp application-production.properties ./config/
```

**3. Run Container:**
```bash
docker run -d \
  --name noah-backup \
  --network noah-backup-network \
  -p 8080:8080 \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e NOAH_BACKUP_S3_BUCKET=noah-backup-prod \
  -e NOAH_BACKUP_S3_ACCESS_KEY=${AWS_ACCESS_KEY_ID} \
  -e NOAH_BACKUP_S3_SECRET_KEY=${AWS_SECRET_ACCESS_KEY} \
  -e JWT_SECRET=${JWT_SECRET} \
  -v ./config:/app/config:ro \
  -v ./data:/app/data \
  -v ./logs:/app/logs \
  --restart unless-stopped \
  --memory 4g \
  --cpus 2 \
  --health-cmd="curl -f http://localhost:8080/actuator/health || exit 1" \
  --health-interval=30s \
  --health-timeout=10s \
  --health-retries=3 \
  --health-start-period=60s \
  noahbackup/noah-backup:latest
```

### Docker Compose

**docker-compose.yml:**
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
      - NOAH_BACKUP_S3_BUCKET=noah-backup-prod
      - NOAH_BACKUP_S3_ACCESS_KEY=${AWS_ACCESS_KEY_ID}
      - NOAH_BACKUP_S3_SECRET_KEY=${AWS_SECRET_ACCESS_KEY}
      - JWT_SECRET=${JWT_SECRET}
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/noah_backup
      - SPRING_DATASOURCE_USERNAME=noah_backup
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
      - RATE_LIMIT_REDIS_URL=redis://redis:6379
      - SLACK_WEBHOOK_URL=${SLACK_WEBHOOK_URL}
    volumes:
      - ./config:/app/config:ro
      - noah-backup-data:/app/data
      - noah-backup-logs:/app/logs
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    deploy:
      resources:
        limits:
          memory: 4G
          cpus: '2.0'
        reservations:
          memory: 2G
          cpus: '1.0'

  postgres:
    image: postgres:15-alpine
    container_name: noah-backup-db
    restart: unless-stopped
    environment:
      - POSTGRES_DB=noah_backup
      - POSTGRES_USER=noah_backup
      - POSTGRES_PASSWORD=${DB_PASSWORD}
      - POSTGRES_INITDB_ARGS=--auth-host=scram-sha-256
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U noah_backup -d noah_backup"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 30s
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '1.0'

  redis:
    image: redis:7-alpine
    container_name: noah-backup-redis
    restart: unless-stopped
    command: redis-server --appendonly yes --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "--raw", "incr", "ping"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 30s

  prometheus:
    image: prom/prometheus:latest
    container_name: noah-backup-prometheus
    restart: unless-stopped
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - prometheus-data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/usr/share/prometheus/console_libraries'
      - '--web.console.templates=/usr/share/prometheus/consoles'

  grafana:
    image: grafana/grafana:latest
    container_name: noah-backup-grafana
    restart: unless-stopped
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_PASSWORD}
    volumes:
      - grafana-data:/var/lib/grafana
      - ./grafana/dashboards:/etc/grafana/provisioning/dashboards:ro
      - ./grafana/datasources:/etc/grafana/provisioning/datasources:ro

volumes:
  noah-backup-data:
    driver: local
  noah-backup-logs:
    driver: local
  postgres-data:
    driver: local
  redis-data:
    driver: local
  prometheus-data:
    driver: local
  grafana-data:
    driver: local

networks:
  default:
    name: noah-backup-network
```

**Deploy with Docker Compose:**
```bash
# Create environment file
cp .env.example .env
# Edit .env with your configuration

# Deploy stack
docker-compose up -d

# Check status
docker-compose ps
docker-compose logs noah-backup
```

## Kubernetes Deployment

### Namespace and Configuration

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

**configmap.yaml:**
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: noah-backup-config
  namespace: noah-backup
data:
  application-production.properties: |
    server.port=8080
    management.server.port=8081
    management.endpoints.web.exposure.include=health,info,metrics,prometheus
    
    # Logging
    logging.level.com.noahbackup=INFO
    logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
    
    # Security
    noah.security.strict-mode=true
    noah.security.audit.enabled=true
    noah.security.ratelimit.enabled=true
    
    # Backup Configuration
    noah.backup.enabled=true
    noah.backup.schedule.daily=0 2 * * *
    noah.backup.schedule.weekly=0 3 * * 0
    
    # Notifications
    noah.notifications.enabled=true
    noah.notifications.channels=log,slack
```

**secrets.yaml:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: noah-backup-secrets
  namespace: noah-backup
type: Opaque
data:
  # Base64 encoded values
  s3-access-key: QUtJQS4uLg==  # echo -n "AKIA..." | base64
  s3-secret-key: d0phbHIuLi4=  # echo -n "wJalr..." | base64
  jwt-secret: eW91ci1zdXBlci1zZWN1cmUtand0LXNlY3JldA==
  slack-webhook-url: aHR0cHM6Ly9ob29rcy5zbGFjay5jb20vLi4u
```

### Deployment and Service

**deployment.yaml:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: noah-backup
  namespace: noah-backup
  labels:
    app: noah-backup
    version: v1.0.0
spec:
  replicas: 2
  selector:
    matchLabels:
      app: noah-backup
  template:
    metadata:
      labels:
        app: noah-backup
        version: v1.0.0
    spec:
      serviceAccountName: noah-backup
      securityContext:
        fsGroup: 1001
        runAsNonRoot: true
        runAsUser: 1001
      containers:
      - name: noah-backup
        image: noahbackup/noah-backup:1.0.0
        imagePullPolicy: IfNotPresent
        ports:
        - name: http
          containerPort: 8080
          protocol: TCP
        - name: management
          containerPort: 8081
          protocol: TCP
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: JAVA_OPTS
          value: "-Xms2g -Xmx4g -XX:+UseG1GC -XX:+UseContainerSupport"
        - name: NOAH_BACKUP_S3_BUCKET
          value: "noah-backup-prod"
        - name: NOAH_BACKUP_S3_ACCESS_KEY
          valueFrom:
            secretKeyRef:
              name: noah-backup-secrets
              key: s3-access-key
        - name: NOAH_BACKUP_S3_SECRET_KEY
          valueFrom:
            secretKeyRef:
              name: noah-backup-secrets
              key: s3-secret-key
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: noah-backup-secrets
              key: jwt-secret
        - name: SLACK_WEBHOOK_URL
          valueFrom:
            secretKeyRef:
              name: noah-backup-secrets
              key: slack-webhook-url
        volumeMounts:
        - name: config
          mountPath: /app/config
          readOnly: true
        - name: data
          mountPath: /app/data
        - name: logs
          mountPath: /app/logs
        resources:
          requests:
            memory: "2Gi"
            cpu: "500m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: management
          initialDelaySeconds: 60
          periodSeconds: 30
          timeoutSeconds: 10
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: management
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        startupProbe:
          httpGet:
            path: /actuator/health/liveness
            port: management
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 10
      volumes:
      - name: config
        configMap:
          name: noah-backup-config
      - name: data
        persistentVolumeClaim:
          claimName: noah-backup-data
      - name: logs
        persistentVolumeClaim:
          claimName: noah-backup-logs
      terminationGracePeriodSeconds: 30
```

**service.yaml:**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: noah-backup-service
  namespace: noah-backup
  labels:
    app: noah-backup
spec:
  type: ClusterIP
  ports:
  - port: 8080
    targetPort: http
    protocol: TCP
    name: http
  - port: 8081
    targetPort: management
    protocol: TCP
    name: management
  selector:
    app: noah-backup

---
apiVersion: v1
kind: Service
metadata:
  name: noah-backup-metrics
  namespace: noah-backup
  labels:
    app: noah-backup
  annotations:
    prometheus.io/scrape: "true"
    prometheus.io/port: "8081"
    prometheus.io/path: "/actuator/prometheus"
spec:
  type: ClusterIP
  ports:
  - port: 8081
    targetPort: management
    protocol: TCP
    name: metrics
  selector:
    app: noah-backup
```

**ingress.yaml:**
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: noah-backup-ingress
  namespace: noah-backup
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/proxy-body-size: "100m"
    nginx.ingress.kubernetes.io/rate-limit: "60"
    nginx.ingress.kubernetes.io/rate-limit-window: "1m"
spec:
  tls:
  - hosts:
    - noah-backup.company.com
    secretName: noah-backup-tls
  rules:
  - host: noah-backup.company.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: noah-backup-service
            port:
              number: 8080
```

### Persistent Storage

**pvc.yaml:**
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: noah-backup-data
  namespace: noah-backup
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: fast-ssd
  resources:
    requests:
      storage: 100Gi

---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: noah-backup-logs
  namespace: noah-backup
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: standard
  resources:
    requests:
      storage: 50Gi
```

### Deploy to Kubernetes

```bash
# Apply all configurations
kubectl apply -f namespace.yaml
kubectl apply -f configmap.yaml
kubectl apply -f secrets.yaml
kubectl apply -f pvc.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f ingress.yaml

# Check deployment status
kubectl get pods -n noah-backup
kubectl describe deployment noah-backup -n noah-backup
kubectl logs -f deployment/noah-backup -n noah-backup

# Check service
kubectl get svc -n noah-backup
kubectl port-forward svc/noah-backup-service 8080:8080 -n noah-backup
```

## High Availability Setup

### Load Balancer Configuration

**HAProxy Configuration:**
```haproxy
global
    daemon
    chroot /var/lib/haproxy
    stats socket /run/haproxy/admin.sock mode 660 level admin
    stats timeout 30s
    user haproxy
    group haproxy

defaults
    mode http
    timeout connect 5000ms
    timeout client  50000ms
    timeout server  50000ms
    option httplog
    option dontlognull

frontend noah-backup-frontend
    bind *:80
    bind *:443 ssl crt /etc/ssl/certs/noah-backup.pem
    redirect scheme https if !{ ssl_fc }
    
    # Security headers
    http-response set-header X-Frame-Options DENY
    http-response set-header X-Content-Type-Options nosniff
    http-response set-header Strict-Transport-Security "max-age=31536000; includeSubDomains"
    
    default_backend noah-backup-backend

backend noah-backup-backend
    balance roundrobin
    option httpchk GET /actuator/health
    http-check expect status 200
    
    server noah-backup-1 10.0.1.10:8080 check
    server noah-backup-2 10.0.1.11:8080 check
    server noah-backup-3 10.0.1.12:8080 check
```

### Database Clustering

**PostgreSQL Primary-Replica Setup:**
```yaml
# Primary Database
apiVersion: postgresql.cnpg.io/v1
kind: Cluster
metadata:
  name: noah-backup-postgres
  namespace: noah-backup
spec:
  instances: 3
  primaryUpdateStrategy: unsupervised
  
  postgresql:
    parameters:
      max_connections: "200"
      shared_buffers: "256MB"
      effective_cache_size: "1GB"
      
  bootstrap:
    initdb:
      database: noah_backup
      owner: noah_backup
      secret:
        name: noah-backup-postgres-credentials
        
  storage:
    size: 100Gi
    storageClass: fast-ssd
    
  monitoring:
    enabled: true
```

## Monitoring and Alerting

### Prometheus Configuration

**prometheus.yml:**
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "/etc/prometheus/rules/*.yml"

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
          names:
            - noah-backup
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_port]
        action: replace
        target_label: __address__
        regex: ([^:]+)(?::\d+)?;(\d+)
        replacement: $1:$2

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093
```

### Alert Rules

**alert-rules.yml:**
```yaml
groups:
- name: noah-backup
  rules:
  - alert: NoahBackupDown
    expr: up{job="noah-backup"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "Noah Backup instance is down"
      description: "Noah Backup instance {{ $labels.instance }} has been down for more than 1 minute."

  - alert: NoahBackupHighMemoryUsage
    expr: (jvm_memory_used_bytes{job="noah-backup"} / jvm_memory_max_bytes{job="noah-backup"}) * 100 > 80
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "High memory usage on Noah Backup"
      description: "Memory usage is above 80% on {{ $labels.instance }}"

  - alert: NoahBackupBackupFailure
    expr: increase(backup_operations_failed_total[5m]) > 0
    for: 0m
    labels:
      severity: critical
    annotations:
      summary: "Backup operation failed"
      description: "A backup operation has failed in the last 5 minutes"

  - alert: NoahBackupHighSecurityThreats
    expr: increase(security_threats_detected_total[1h]) > 10
    for: 0m
    labels:
      severity: warning
    annotations:
      summary: "High number of security threats detected"
      description: "More than 10 security threats detected in the last hour"
```

### Grafana Dashboard

**dashboard.json (excerpt):**
```json
{
  "dashboard": {
    "title": "Noah Backup Monitoring",
    "panels": [
      {
        "title": "Backup Success Rate",
        "type": "stat",
        "targets": [
          {
            "expr": "(backup_operations_successful_total / backup_operations_total) * 100"
          }
        ]
      },
      {
        "title": "Memory Usage",
        "type": "graph",
        "targets": [
          {
            "expr": "jvm_memory_used_bytes{job=\"noah-backup\"}"
          }
        ]
      },
      {
        "title": "Security Threats",
        "type": "graph",
        "targets": [
          {
            "expr": "increase(security_threats_detected_total[5m])"
          }
        ]
      }
    ]
  }
}
```

## Backup and Disaster Recovery

### Configuration Backup

**Backup Script:**
```bash
#!/bin/bash
# backup-noah-config.sh

BACKUP_DATE=$(date +"%Y%m%d-%H%M%S")
BACKUP_DIR="/backups/noah-backup-config-$BACKUP_DATE"

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Backup configuration files
cp -r /opt/noah-backup/config "$BACKUP_DIR/"

# Backup database (if using PostgreSQL)
pg_dump -h localhost -U noah_backup noah_backup > "$BACKUP_DIR/database.sql"

# Backup Kubernetes resources (if using K8s)
kubectl get all -n noah-backup -o yaml > "$BACKUP_DIR/k8s-resources.yaml"
kubectl get secrets -n noah-backup -o yaml > "$BACKUP_DIR/k8s-secrets.yaml"
kubectl get configmaps -n noah-backup -o yaml > "$BACKUP_DIR/k8s-configmaps.yaml"

# Create archive
tar -czf "/backups/noah-backup-config-$BACKUP_DATE.tar.gz" -C /backups "noah-backup-config-$BACKUP_DATE"
rm -rf "$BACKUP_DIR"

echo "Configuration backup completed: noah-backup-config-$BACKUP_DATE.tar.gz"
```

### Disaster Recovery Plan

**Recovery Steps:**
1. **Assess Impact**: Determine scope of failure
2. **Restore Infrastructure**: Rebuild servers/containers
3. **Restore Configuration**: Apply backed-up configuration
4. **Restore Database**: Restore from database backup
5. **Verify Functionality**: Test all critical functions
6. **Resume Operations**: Enable backup schedules

**Recovery Time Objectives:**
- **Configuration Recovery**: 30 minutes
- **Database Recovery**: 1 hour
- **Full System Recovery**: 2 hours
- **Data Recovery**: 4 hours (depending on backup size)

## Performance Tuning

### JVM Tuning

**Production JVM Settings:**
```bash
JAVA_OPTS="-Xms4g -Xmx8g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseContainerSupport \
  -XX:+PrintGC \
  -XX:+PrintGCTimeStamps \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/app/logs/heapdump.hprof \
  -Dspring.jmx.enabled=true \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9999 \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false"
```

### Database Tuning

**PostgreSQL Configuration:**
```sql
-- Connection settings
max_connections = 200
shared_buffers = 2GB
effective_cache_size = 6GB
maintenance_work_mem = 512MB
work_mem = 16MB

-- Write performance
wal_buffers = 16MB
checkpoint_completion_target = 0.7
wal_compression = on

-- Query optimization
random_page_cost = 1.1
effective_io_concurrency = 200
```

### Storage Optimization

**File System Configuration:**
```bash
# For SSD storage
echo mq-deadline > /sys/block/sda/queue/scheduler

# Mount options for performance
mount -o noatime,nodiratime,discard /dev/sda1 /opt/noah-backup/data
```

## Security Hardening

### Network Security

**Firewall Rules (iptables):**
```bash
# Allow SSH (for management)
iptables -A INPUT -p tcp --dport 22 -j ACCEPT

# Allow Noah Backup API
iptables -A INPUT -p tcp --dport 8080 -j ACCEPT

# Allow management port (restrict to monitoring network)
iptables -A INPUT -p tcp --dport 8081 -s 10.0.2.0/24 -j ACCEPT

# Drop everything else
iptables -A INPUT -j DROP
```

### Application Security

**Security Configuration:**
```properties
# Strict security mode
noah.security.strict-mode=true

# Strong JWT secrets (256+ bits)
noah.security.jwt.secret=${JWT_SECRET}

# Rate limiting enabled
noah.security.ratelimit.enabled=true
noah.security.ratelimit.aggressive-threshold=3

# Audit logging
noah.security.audit.enabled=true
noah.security.audit.include-request-details=true

# Input validation
noah.security.validation.strict=true
noah.security.validation.block-on-threat=true
```

## Troubleshooting

### Common Issues

**1. Application Won't Start:**
```bash
# Check Java version
java -version

# Check configuration
java -jar noah-backup.jar --spring.config.location=file:./application.properties --debug

# Check logs
tail -f /opt/noah-backup/logs/noah-backup.log
```

**2. S3 Connection Issues:**
```bash
# Test S3 connectivity
aws s3 ls s3://your-bucket-name --region us-east-1

# Check credentials
echo $AWS_ACCESS_KEY_ID
echo $AWS_SECRET_ACCESS_KEY

# Verify network connectivity
curl -I https://s3.amazonaws.com
```

**3. High Memory Usage:**
```bash
# Monitor JVM memory
jcmd <pid> VM.memory_summary
jcmd <pid> GC.run

# Generate heap dump
jcmd <pid> GC.dump_heap /tmp/heapdump.hprof
```

**4. Authentication Issues:**
```bash
# Check JWT configuration
curl -X POST http://localhost:8080/api/v1/auth/status

# Test login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123!"}'
```

### Health Checks

**Application Health:**
```bash
# Basic health check
curl http://localhost:8081/actuator/health

# Detailed health check
curl http://localhost:8081/actuator/health?showDetails=true

# Backup system health
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/backup/health
```

### Log Analysis

**Important Log Locations:**
- Application logs: `/app/logs/noah-backup.log`
- Audit logs: `/app/logs/audit.log` 
- Security logs: `/app/logs/security.log`
- System logs: `/var/log/syslog` or journalctl

**Log Analysis Commands:**
```bash
# Monitor application logs
tail -f /app/logs/noah-backup.log

# Search for errors
grep -i error /app/logs/noah-backup.log

# Analyze security events
grep -i "security" /app/logs/audit.log | tail -20

# Monitor real-time logs
journalctl -u noah-backup -f
```

## Maintenance

### Regular Maintenance Tasks

**Weekly:**
- Review backup success rates
- Check storage utilization
- Monitor security alerts
- Update dependencies (if needed)

**Monthly:**
- Review and rotate logs
- Update SSL certificates
- Conduct security scans
- Performance review and tuning

**Quarterly:**
- Full security audit
- Disaster recovery testing
- Capacity planning review
- Update documentation

### Update Procedures

**Rolling Update (Kubernetes):**
```bash
# Update deployment image
kubectl set image deployment/noah-backup noah-backup=noahbackup/noah-backup:1.1.0 -n noah-backup

# Monitor rollout
kubectl rollout status deployment/noah-backup -n noah-backup

# Rollback if needed
kubectl rollout undo deployment/noah-backup -n noah-backup
```

**Zero-Downtime Update (Docker):**
```bash
# Pull new image
docker pull noahbackup/noah-backup:1.1.0

# Start new container
docker run -d --name noah-backup-new \
  [same configuration as before] \
  noahbackup/noah-backup:1.1.0

# Update load balancer
# Stop old container
docker stop noah-backup
docker rm noah-backup
docker rename noah-backup-new noah-backup
```

---

## Support

For deployment assistance and enterprise support:
- **Email**: deployment@noah-backup.com
- **Documentation**: https://docs.noah-backup.com
- **Professional Services**: https://noah-backup.com/services