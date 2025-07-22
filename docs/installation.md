# Noah Backup - Installation Guide

## 📋 Table of Contents

- [System Requirements](#system-requirements)
- [Installation Methods](#installation-methods)
- [Service Installation](#service-installation)
- [Verification](#verification)
- [Post-Installation](#post-installation)

## System Requirements

### Minimum Requirements
- **Operating System**: Windows 10/11, Windows Server 2016+, Linux (Docker)
- **Java**: OpenJDK or Oracle JDK 17+
- **Memory**: 4 GB RAM
- **Storage**: 100 GB available space
- **Network**: Outbound HTTPS access to S3 endpoints

### Recommended Production
- **Operating System**: Windows Server 2019+, Ubuntu 20.04+, RHEL 8+
- **Java**: OpenJDK 17+ with G1 garbage collector
- **Memory**: 8+ GB RAM
- **Storage**: 500+ GB SSD
- **Network**: Dedicated network with high bandwidth

## Installation Methods

### Method 1: JAR Installation (Recommended)

#### 1. Download Release
```bash
# Download latest release
wget https://github.com/your-org/noah-backup/releases/latest/download/noah-backup.jar

# Or build from source
git clone https://github.com/your-org/noah-backup.git
cd noah-backup
./gradlew clean build bootJar
```

#### 2. Create Installation Directory
```bash
# Linux/macOS
sudo mkdir -p /opt/noah-backup/{config,data,logs}
sudo useradd -r -s /bin/false -d /opt/noah-backup noah-backup
sudo chown -R noah-backup:noah-backup /opt/noah-backup

# Windows
New-Item -Path "C:\Program Files\Noah Backup" -ItemType Directory -Force
```

#### 3. Install Application
```bash
# Linux/macOS
sudo cp noah-backup.jar /opt/noah-backup/
sudo chmod 644 /opt/noah-backup/noah-backup.jar

# Windows
Copy-Item noah-backup.jar "C:\Program Files\Noah Backup\"
```

#### 4. Create Configuration
```bash
# Copy example configuration
cp demo-application/src/main/resources/application.yml application.yml
# Edit configuration (see Configuration section)
```

### Method 2: Docker Installation

#### 1. Pull Docker Image
```bash
docker pull noahbackup/noah-backup:latest
```

#### 2. Create Configuration
```bash
mkdir -p ./config ./data ./logs
# Create configuration file in ./config/application.yml
```

#### 3. Run Container
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

## Service Installation

### Linux (systemd)

#### Create Service File
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

### Windows Service

#### Using NSSM (Non-Sucking Service Manager)
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

## Verification

### 1. Check Service Status
```bash
# Linux
sudo systemctl status noah-backup

# Windows
Get-Service "Noah Backup"

# Docker
docker ps | grep noah-backup
```

### 2. Test Application
```bash
# Health check
curl http://localhost:8080/actuator/health

# API status
curl http://localhost:8080/api/v1/auth/status
```

## Post-Installation

### 1. Initial Configuration
- Configure S3 storage credentials
- Set up backup paths
- Configure notification channels
- Set JWT secret for production

### 2. Security Setup
- Change default passwords
- Configure SSL certificates
- Set up firewall rules
- Enable audit logging

### 3. Monitoring Setup
- Configure health checks
- Set up log rotation
- Configure alerting
- Set up backup verification

---

For detailed configuration instructions, see the [Configuration Guide](configuration.md).
For deployment options, see the [Deployment Guide](deployment.md).