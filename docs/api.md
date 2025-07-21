# 📚 Noah Backup - API Documentation

## Overview

The Noah Backup REST API provides comprehensive programmatic access to all backup operations, configuration management, and system monitoring. The API follows RESTful principles and uses JWT tokens for authentication.

## Base URL

```
http://localhost:8080/api/v1
```

## Authentication

All API endpoints (except health checks) require authentication using JWT Bearer tokens.

### Getting a Token

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123!"
}
```

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIkFETUlOIiwiQkFDS1VQX0FETUlOIl0sImlhdCI6MTcwNTMzNjgwMCwiZXhwIjoxNzA1NDIzMjAwfQ.abc123...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInR5cGUiOiJyZWZyZXNoIiwiaWF0IjoxNzA1MzM2ODAwLCJleHAiOjE3MDU5NDE2MDB9.def456...",
  "username": "admin",
  "roles": ["ADMIN", "BACKUP_ADMIN"],
  "expiresIn": 86400000,
  "timestamp": "2024-01-15T10:30:00"
}
```

### Using the Token

Include the JWT token in the Authorization header for all requests:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiI...
```

## User Roles

| Role | Permissions | Description |
|------|-------------|-------------|
| `BACKUP_ADMIN` | Full access | Create, read, update, delete all resources |
| `BACKUP_USER` | Limited write | Start backups, read status and configuration |
| `BACKUP_VIEWER` | Read-only | View status, configuration, and reports |

## API Endpoints

### Authentication Endpoints

#### POST /api/v1/auth/login
Authenticate user and receive JWT tokens.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Responses:**
- `200 OK`: Authentication successful
- `401 Unauthorized`: Invalid credentials
- `500 Internal Server Error`: Authentication system error

#### POST /api/v1/auth/refresh
Refresh an expired JWT token using a refresh token.

**Headers:**
```http
Authorization: Bearer <refresh_token>
```

**Response:**
```json
{
  "success": true,
  "token": "new_jwt_token",
  "refreshToken": "new_refresh_token",
  "expiresIn": 86400000,
  "timestamp": "2024-01-15T10:30:00"
}
```

#### GET /api/v1/auth/me
Get current user information from JWT token.

**Headers:**
```http
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "authenticated": true,
  "username": "admin",
  "roles": ["ADMIN", "BACKUP_ADMIN"],
  "expiresIn": 82800000,
  "tokenExpiring": false,
  "timestamp": "2024-01-15T10:30:00"
}
```

#### POST /api/v1/auth/logout
Logout user (client should discard token).

**Response:**
```json
{
  "success": true,
  "message": "Logout successful - please remove token from client",
  "timestamp": "2024-01-15T10:30:00"
}
```

#### GET /api/v1/auth/status
Check authentication system status.

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "authenticationEnabled": true,
  "jwtEnabled": true,
  "refreshTokenEnabled": true,
  "status": "UP"
}
```

### Backup Operations

#### POST /api/v1/backup/start
Start a manual backup operation.

**Required Role:** `BACKUP_ADMIN` or `BACKUP_USER`

**Request Body (Optional):**
```json
{
  "paths": ["C:\\Data", "C:\\Users"],
  "description": "Manual backup of critical data",
  "priority": "HIGH"
}
```

**Response:**
```json
{
  "success": true,
  "jobType": "MANUAL_BACKUP",
  "successCount": 1247,
  "failureCount": 0,
  "message": "Backup completed successfully",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Status Codes:**
- `200 OK`: Backup completed successfully
- `409 Conflict`: Backup already running
- `500 Internal Server Error`: Backup failed

#### GET /api/v1/backup/status
Get current backup system status.

**Required Role:** Any authenticated user

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "enabled": true,
  "running": false,
  "weeklyEnabled": true,
  "backupPathsCount": 2,
  "bucket": "noah-backup-prod",
  "timeoutMinutes": 60,
  "dailySchedule": "0 2 * * *",
  "weeklySchedule": "0 3 * * 0"
}
```

#### POST /api/v1/backup/stop
Attempt to stop a running backup operation.

**Required Role:** `BACKUP_ADMIN`

**Response:**
```json
{
  "success": false,
  "message": "Graceful backup stop not yet implemented - backup will complete normally",
  "timestamp": "2024-01-15T10:30:00"
}
```

#### GET /api/v1/backup/history
Get recent backup operation history.

**Required Role:** Any authenticated user

**Query Parameters:**
- `limit` (optional): Number of records to return (default: 10, max: 100)

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "message": "Backup history tracking not yet implemented",
  "limit": 10,
  "history": []
}
```

#### GET /api/v1/backup/health
Detailed health check for backup system components.

**Required Role:** Any authenticated user

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": "UP",
  "components": {
    "scheduler": "UP",
    "configuration": "UP",
    "backup_operation": "READY"
  },
  "backup_paths": 2,
  "enabled": true
}
```

### Configuration Management

#### GET /api/v1/config
Get current backup configuration (sanitized).

**Required Role:** Any authenticated user

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "enabled": true,
  "weeklyEnabled": true,
  "backupPaths": ["C:\\Data", "C:\\Users"],
  "bucket": "noah-backup-prod",
  "timeoutMinutes": 60,
  "dailySchedule": "0 2 * * *",
  "weeklySchedule": "0 3 * * 0"
}
```

#### GET /api/v1/config/system
Get system-level configuration and capabilities.

**Required Role:** `BACKUP_ADMIN`

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "java_version": "17.0.5",
  "os_name": "Windows 11",
  "app_version": "1.0.0",
  "features": {
    "vss_support": true,
    "encryption_support": true,
    "s3_support": true,
    "scheduling_support": true,
    "api_support": true
  },
  "limits": {
    "max_backup_paths": 100,
    "max_concurrent_backups": 1,
    "max_file_size_gb": 50,
    "api_rate_limit_per_hour": 1000
  }
}
```

#### POST /api/v1/config/schedule
Update backup schedule configuration.

**Required Role:** `BACKUP_ADMIN`

**Request Body:**
```json
{
  "dailySchedule": "0 3 * * *",
  "weeklySchedule": "0 4 * * 0",
  "enabled": true,
  "weeklyEnabled": true
}
```

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "success": false,
  "message": "Schedule update not implemented - configuration is read-only"
}
```

#### GET /api/v1/config/validate
Validate current configuration for issues.

**Required Role:** `BACKUP_ADMIN` or `BACKUP_USER`

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "valid": true,
  "checks": {
    "backup_paths": "OK: 2 paths configured",
    "s3_bucket": "OK: Bucket configured",
    "scheduling": "OK: Scheduling enabled",
    "timeout": "OK: Timeout configured"
  }
}
```

### Reporting Endpoints

#### GET /api/v1/reports/daily/{date}
Get backup reports for a specific date.

**Required Role:** Any authenticated user

**Path Parameters:**
- `date`: Date in YYYY-MM-DD format

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "date": "2024-01-15",
  "message": "Daily report functionality will be available when report-core is integrated"
}
```

#### GET /api/v1/reports/history
Get recent backup operation history.

**Required Role:** Any authenticated user

**Query Parameters:**
- `days` (optional): Number of days to retrieve (default: 7, min: 1, max: 90)

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "days": 7,
  "message": "Backup history functionality will be available when report-core is integrated"
}
```

#### GET /api/v1/reports/statistics
Get backup statistics for a date range.

**Required Role:** Any authenticated user

**Query Parameters:**
- `days` (optional): Number of days for statistics (default: 30, min: 1, max: 365)

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "period_start": "2023-12-16",
  "period_end": "2024-01-15",
  "days": 30,
  "message": "Statistics functionality will be available when report-core is integrated"
}
```

#### GET /api/v1/reports/available-dates
Get list of dates for which reports are available.

**Required Role:** Any authenticated user

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "message": "Available dates functionality will be available when report-core is integrated"
}
```

#### GET /api/v1/reports/notifications/status
Get notification system status.

**Required Role:** `BACKUP_ADMIN`

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "message": "Notification status will be available when report-core is integrated"
}
```

#### POST /api/v1/reports/notifications/test
Send a test notification to all configured channels.

**Required Role:** `BACKUP_ADMIN`

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "success": true,
  "message": "Test notification functionality will be available when report-core is integrated"
}
```

#### POST /api/v1/reports/cleanup
Trigger cleanup of old report files.

**Required Role:** `BACKUP_ADMIN`

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "message": "Report cleanup functionality will be available when report-core is integrated"
}
```

#### GET /api/v1/reports/health
Health check for reporting system.

**Required Role:** Any authenticated user

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": "UP",
  "message": "Reporting health check will be enhanced when report-core is integrated",
  "reports_api": "UP",
  "configuration": "UP"
}
```

## Error Responses

All API endpoints return consistent error responses:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'username'",
  "path": "/api/v1/auth/login"
}
```

### Common HTTP Status Codes

- `200 OK`: Request successful
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required or failed
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource conflict (e.g., backup already running)
- `429 Too Many Requests`: Rate limit exceeded
- `500 Internal Server Error`: Server error
- `503 Service Unavailable`: Service temporarily unavailable

## Rate Limiting

The API implements multi-level rate limiting:

### Default Limits

| Endpoint Type | Limit | Window |
|---------------|-------|---------|
| Authentication | 10 requests | 1 minute |
| Backup Operations | 50 requests | 1 hour |
| Admin Operations | 30 requests | 1 minute |
| General API | 60 requests | 1 minute |

### Rate Limit Headers

Response headers include rate limiting information:

```http
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 45
X-RateLimit-Reset: 1705336860
```

### Rate Limit Exceeded

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Try again in 30 seconds.",
  "retryAfter": 30
}
```

## API Examples

### Complete Authentication Flow

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123!"}' | \
  jq -r '.token')

# 2. Use token for authenticated request
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/backup/status

# 3. Start a backup
curl -X POST http://localhost:8080/api/v1/backup/start \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"description":"API test backup"}'
```

### Configuration Check

```bash
# Check system configuration
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/config/system

# Validate configuration
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/config/validate
```

### Health Monitoring

```bash
# General health
curl http://localhost:8080/actuator/health

# Backup system health
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/backup/health

# Reporting health
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/reports/health
```

## SDK and Client Libraries

### cURL Examples
All examples in this documentation use cURL for maximum compatibility.

### Postman Collection
Download the Postman collection: [Noah Backup API.postman_collection.json](postman/Noah_Backup_API.postman_collection.json)

### OpenAPI Specification
The complete API specification is available at: `/v3/api-docs` when the application is running.

### Interactive Documentation
Swagger UI is available at: `/swagger-ui.html` for interactive API exploration.

## Webhooks

Noah Backup can send webhook notifications for backup events:

### Webhook Configuration

```json
{
  "url": "https://your-server.com/webhooks/noah-backup",
  "events": ["backup.started", "backup.completed", "backup.failed"],
  "secret": "your-webhook-secret"
}
```

### Webhook Payload

```json
{
  "event": "backup.completed",
  "timestamp": "2024-01-15T10:30:00Z",
  "data": {
    "operationId": "backup-20240115-103000",
    "success": true,
    "filesProcessed": 1247,
    "duration": "00:15:33",
    "bucket": "noah-backup-prod"
  }
}
```

## Security Considerations

### HTTPS Only
Always use HTTPS in production to protect JWT tokens and sensitive data.

### Token Storage
- Store JWT tokens securely on the client side
- Never log or expose tokens in URLs
- Implement proper token refresh logic

### CORS Configuration
Configure CORS appropriately for your frontend applications:

```properties
noah.api.cors.allowed-origins=https://your-frontend.com
noah.api.cors.allowed-methods=GET,POST,PUT,DELETE
noah.api.cors.allowed-headers=Authorization,Content-Type
```

### Input Validation
All input is validated against:
- SQL injection patterns
- XSS attack vectors
- Path traversal attempts
- Command injection patterns

### Audit Logging
All API calls are logged with:
- User identification
- IP address
- Request parameters (sanitized)
- Response status
- Timestamp

---

## Support

For API support and questions:
- **GitHub Issues**: [Report API issues](https://github.com/your-org/noah-backup/issues)
- **Documentation**: [Complete documentation](https://docs.noah-backup.com)
- **Email**: api-support@noah-backup.com