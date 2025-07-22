#!/bin/bash

# Noah Backup Configuration Validator
# This script validates your configuration before running the application

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
ENV_FILE=".env"
ENV_EXAMPLE=".env.example"
REQUIRED_VARS=(
    "AWS_ACCESS_KEY_ID"
    "AWS_SECRET_ACCESS_KEY"
    "AWS_REGION"
    "S3_BUCKET_NAME"
    "JWT_SECRET"
)

OPTIONAL_VARS=(
    "NOAH_BACKUP_PATHS"
    "NOAH_SCHEDULE_DAILY"
    "NOAH_SCHEDULE_WEEKLY"
    "SMTP_HOST"
    "SLACK_WEBHOOK_URL"
)

echo -e "${BLUE}🔍 Noah Backup Configuration Validator${NC}"
echo "=============================================="

# Check if .env file exists
if [ ! -f "$ENV_FILE" ]; then
    echo -e "${RED}❌ .env file not found!${NC}"
    echo -e "${YELLOW}💡 Run: cp $ENV_EXAMPLE $ENV_FILE${NC}"
    echo -e "${YELLOW}💡 Then edit $ENV_FILE with your configuration${NC}"
    exit 1
fi

echo -e "${GREEN}✅ .env file found${NC}"

# Load environment variables
source "$ENV_FILE"

# Validation functions
validate_required_vars() {
    echo -e "\n${BLUE}🔐 Validating Required Variables${NC}"
    local errors=0
    
    for var in "${REQUIRED_VARS[@]}"; do
        if [ -z "${!var}" ] || [ "${!var}" == "your-"* ] || [ "${!var}" == "NoahBackupJWTSecretKey2024!"* ]; then
            echo -e "${RED}❌ $var is not set or uses default value${NC}"
            errors=$((errors + 1))
        else
            echo -e "${GREEN}✅ $var${NC}"
        fi
    done
    
    return $errors
}

validate_jwt_secret() {
    echo -e "\n${BLUE}🔑 Validating JWT Secret${NC}"
    
    if [ ${#JWT_SECRET} -lt 32 ]; then
        echo -e "${RED}❌ JWT_SECRET is too short (minimum 32 characters)${NC}"
        echo -e "${YELLOW}💡 Generate a stronger secret: openssl rand -base64 64${NC}"
        return 1
    fi
    
    if [ "$JWT_SECRET" == "NoahBackupJWTSecretKey2024!ForSecureAPIAuthentication" ]; then
        echo -e "${RED}❌ JWT_SECRET is using the default value${NC}"
        echo -e "${YELLOW}💡 Generate a new secret: openssl rand -base64 64${NC}"
        return 1
    fi
    
    echo -e "${GREEN}✅ JWT_SECRET is properly configured${NC}"
    return 0
}

validate_s3_config() {
    echo -e "\n${BLUE}☁️  Validating S3 Configuration${NC}"
    local errors=0
    
    # Check S3 bucket name format
    if [[ ! "$S3_BUCKET_NAME" =~ ^[a-z0-9][a-z0-9.-]*[a-z0-9]$ ]]; then
        echo -e "${RED}❌ S3_BUCKET_NAME format is invalid${NC}"
        echo -e "${YELLOW}💡 Bucket names must be 3-63 characters, lowercase, start/end with alphanumeric${NC}"
        errors=$((errors + 1))
    else
        echo -e "${GREEN}✅ S3_BUCKET_NAME format is valid${NC}"
    fi
    
    # Check region format
    if [[ ! "$AWS_REGION" =~ ^[a-z]{2}-[a-z]+-[0-9]+$ ]] && [ "$AWS_REGION" != "us-east-1" ]; then
        echo -e "${YELLOW}⚠️  AWS_REGION format might be incorrect: $AWS_REGION${NC}"
    else
        echo -e "${GREEN}✅ AWS_REGION format looks correct${NC}"
    fi
    
    # Check for MinIO configuration
    if [ -n "$S3_ENDPOINT" ]; then
        echo -e "${BLUE}🔧 Detected custom S3 endpoint (MinIO/other): $S3_ENDPOINT${NC}"
        if [ "$S3_PATH_STYLE_ACCESS" != "true" ]; then
            echo -e "${YELLOW}⚠️  For MinIO, consider setting S3_PATH_STYLE_ACCESS=true${NC}"
        fi
    fi
    
    return $errors
}

validate_backup_paths() {
    echo -e "\n${BLUE}📂 Validating Backup Paths${NC}"
    
    if [ -z "$NOAH_BACKUP_PATHS" ]; then
        echo -e "${YELLOW}⚠️  NOAH_BACKUP_PATHS is not set${NC}"
        echo -e "${YELLOW}💡 Set paths to backup (semicolon-separated)${NC}"
        return 1
    fi
    
    # Split paths by semicolon and validate each
    IFS=';' read -ra PATHS <<< "$NOAH_BACKUP_PATHS"
    local valid_paths=0
    
    for path in "${PATHS[@]}"; do
        path=$(echo "$path" | xargs)  # Trim whitespace
        if [ -d "$path" ] || [[ "$path" == *"%USERNAME%"* ]] || [[ "$path" == *"$USER"* ]]; then
            echo -e "${GREEN}✅ $path${NC}"
            valid_paths=$((valid_paths + 1))
        else
            echo -e "${YELLOW}⚠️  Path may not exist: $path${NC}"
        fi
    done
    
    if [ $valid_paths -gt 0 ]; then
        echo -e "${GREEN}✅ Found $valid_paths backup path(s)${NC}"
        return 0
    else
        echo -e "${RED}❌ No valid backup paths found${NC}"
        return 1
    fi
}

validate_cron_expressions() {
    echo -e "\n${BLUE}⏰ Validating Cron Expressions${NC}"
    
    # Basic cron validation (simplified)
    validate_cron() {
        local cron="$1"
        local name="$2"
        
        # Count parts (should be 6 for Spring cron)
        local parts=$(echo "$cron" | wc -w)
        if [ $parts -ne 6 ]; then
            echo -e "${RED}❌ $name: Invalid cron format (expected 6 parts, got $parts)${NC}"
            return 1
        fi
        
        echo -e "${GREEN}✅ $name: $cron${NC}"
        return 0
    }
    
    local errors=0
    
    if [ -n "$NOAH_SCHEDULE_DAILY" ]; then
        validate_cron "$NOAH_SCHEDULE_DAILY" "Daily schedule" || errors=$((errors + 1))
    fi
    
    if [ -n "$NOAH_SCHEDULE_WEEKLY" ]; then
        validate_cron "$NOAH_SCHEDULE_WEEKLY" "Weekly schedule" || errors=$((errors + 1))
    fi
    
    return $errors
}

validate_optional_config() {
    echo -e "\n${BLUE}📧 Validating Optional Configuration${NC}"
    
    # Email configuration
    if [ "$NOAH_NOTIFICATIONS_EMAIL_ENABLED" == "true" ]; then
        if [ -z "$SMTP_HOST" ] || [ -z "$SMTP_USERNAME" ] || [ -z "$EMAIL_FROM" ]; then
            echo -e "${YELLOW}⚠️  Email notifications enabled but missing SMTP configuration${NC}"
        else
            echo -e "${GREEN}✅ Email notifications configured${NC}"
        fi
    fi
    
    # Slack configuration
    if [ "$NOAH_NOTIFICATIONS_SLACK_ENABLED" == "true" ]; then
        if [ -z "$SLACK_WEBHOOK_URL" ] || [[ "$SLACK_WEBHOOK_URL" == *"YOUR"* ]]; then
            echo -e "${YELLOW}⚠️  Slack notifications enabled but webhook URL not configured${NC}"
        else
            echo -e "${GREEN}✅ Slack notifications configured${NC}"
        fi
    fi
    
    # Teams configuration
    if [ "$NOAH_NOTIFICATIONS_TEAMS_ENABLED" == "true" ]; then
        if [ -z "$TEAMS_WEBHOOK_URL" ] || [[ "$TEAMS_WEBHOOK_URL" == *"YOUR"* ]]; then
            echo -e "${YELLOW}⚠️  Teams notifications enabled but webhook URL not configured${NC}"
        else
            echo -e "${GREEN}✅ Teams notifications configured${NC}"
        fi
    fi
}

test_s3_connectivity() {
    echo -e "\n${BLUE}🌐 Testing S3 Connectivity (Optional)${NC}"
    
    if command -v aws &> /dev/null; then
        echo -e "${BLUE}Testing AWS CLI connectivity...${NC}"
        
        # Export credentials for aws cli
        export AWS_ACCESS_KEY_ID="$AWS_ACCESS_KEY_ID"
        export AWS_SECRET_ACCESS_KEY="$AWS_SECRET_ACCESS_KEY"
        export AWS_DEFAULT_REGION="$AWS_REGION"
        
        if [ -n "$S3_ENDPOINT" ]; then
            echo -e "${BLUE}Using custom endpoint: $S3_ENDPOINT${NC}"
            if aws s3 ls "s3://$S3_BUCKET_NAME" --endpoint-url "$S3_ENDPOINT" &>/dev/null; then
                echo -e "${GREEN}✅ S3 connectivity test passed${NC}"
            else
                echo -e "${YELLOW}⚠️  S3 connectivity test failed (bucket may not exist yet)${NC}"
            fi
        else
            if aws s3 ls "s3://$S3_BUCKET_NAME" &>/dev/null; then
                echo -e "${GREEN}✅ S3 connectivity test passed${NC}"
            else
                echo -e "${YELLOW}⚠️  S3 connectivity test failed (bucket may not exist yet)${NC}"
            fi
        fi
    else
        echo -e "${YELLOW}⚠️  AWS CLI not installed - skipping connectivity test${NC}"
        echo -e "${YELLOW}💡 Install AWS CLI to test S3 connectivity${NC}"
    fi
}

# Main validation
main() {
    local total_errors=0
    
    validate_required_vars || total_errors=$((total_errors + $?))
    validate_jwt_secret || total_errors=$((total_errors + $?))
    validate_s3_config || total_errors=$((total_errors + $?))
    validate_backup_paths || total_errors=$((total_errors + $?))
    validate_cron_expressions || total_errors=$((total_errors + $?))
    validate_optional_config
    
    # Optional S3 connectivity test
    if [ "$1" == "--test-s3" ] || [ "$1" == "-t" ]; then
        test_s3_connectivity
    fi
    
    echo -e "\n${BLUE}📋 Validation Summary${NC}"
    echo "=============================================="
    
    if [ $total_errors -eq 0 ]; then
        echo -e "${GREEN}🎉 Configuration validation passed!${NC}"
        echo -e "${GREEN}🚀 Ready to start Noah Backup${NC}"
        echo ""
        echo -e "${BLUE}Next steps:${NC}"
        echo "1. ./gradlew bootRun (start the application)"
        echo "2. Check logs in ./logs/ directory"
        echo "3. Access API at http://localhost:$PORT (default: 8080)"
        return 0
    else
        echo -e "${RED}❌ Found $total_errors configuration error(s)${NC}"
        echo -e "${YELLOW}💡 Please fix the issues above and run validation again${NC}"
        echo ""
        echo -e "${BLUE}Common fixes:${NC}"
        echo "• Generate new JWT secret: openssl rand -base64 64"
        echo "• Set proper AWS credentials and S3 bucket"
        echo "• Configure backup paths for your environment"
        echo "• Check notification settings if enabled"
        return 1
    fi
}

# Help function
show_help() {
    echo "Noah Backup Configuration Validator"
    echo ""
    echo "Usage: $0 [options]"
    echo ""
    echo "Options:"
    echo "  -h, --help     Show this help message"
    echo "  -t, --test-s3  Test S3 connectivity (requires AWS CLI)"
    echo ""
    echo "Examples:"
    echo "  $0                    # Validate configuration"
    echo "  $0 --test-s3         # Validate and test S3 connectivity"
}

# Parse command line arguments
case "$1" in
    -h|--help)
        show_help
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac