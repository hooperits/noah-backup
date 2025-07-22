#!/bin/bash

# Noah Backup - Easy Setup Script
# This script helps you get started with Noah Backup quickly

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
ENV_FILE=".env"
ENV_EXAMPLE=".env.example"

echo -e "${BLUE}🚀 Noah Backup Easy Setup${NC}"
echo "=============================================="
echo -e "${CYAN}Welcome! This script will help you configure Noah Backup.${NC}"
echo ""

# Function to prompt for user input
prompt_input() {
    local prompt="$1"
    local default="$2"
    local is_secret="$3"
    local value
    
    if [ "$is_secret" == "true" ]; then
        echo -ne "${YELLOW}$prompt${NC}"
        if [ -n "$default" ]; then
            echo -ne " ${CYAN}[default: ***hidden***]${NC}"
        fi
        echo -ne ": "
        read -s value
        echo ""
    else
        echo -ne "${YELLOW}$prompt${NC}"
        if [ -n "$default" ]; then
            echo -ne " ${CYAN}[default: $default]${NC}"
        fi
        echo -ne ": "
        read value
    fi
    
    if [ -z "$value" ] && [ -n "$default" ]; then
        value="$default"
    fi
    
    echo "$value"
}

# Function to prompt yes/no
prompt_yes_no() {
    local prompt="$1"
    local default="$2"
    local response
    
    while true; do
        echo -ne "${YELLOW}$prompt${NC}"
        if [ "$default" == "y" ]; then
            echo -ne " ${CYAN}[Y/n]${NC}: "
        else
            echo -ne " ${CYAN}[y/N]${NC}: "
        fi
        read response
        
        if [ -z "$response" ]; then
            response="$default"
        fi
        
        case "$response" in
            [Yy]|[Yy][Ee][Ss])
                echo "y"
                return
                ;;
            [Nn]|[Nn][Oo])
                echo "n"
                return
                ;;
            *)
                echo -e "${RED}Please answer yes or no.${NC}"
                ;;
        esac
    done
}

# Function to generate secure passwords
generate_password() {
    local length="$1"
    if command -v openssl &> /dev/null; then
        openssl rand -base64 "$length" | tr -d "=+/" | cut -c1-"$length"
    else
        # Fallback method
        < /dev/urandom tr -dc A-Za-z0-9 | head -c"$length"
    fi
}

# Check prerequisites
check_prerequisites() {
    echo -e "${BLUE}🔍 Checking Prerequisites${NC}"
    
    # Check if Java is installed
    if command -v java &> /dev/null; then
        local java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
        echo -e "${GREEN}✅ Java found: $java_version${NC}"
    else
        echo -e "${RED}❌ Java not found${NC}"
        echo -e "${YELLOW}💡 Please install Java 11 or later${NC}"
        exit 1
    fi
    
    # Check if Gradle wrapper exists
    if [ -f "./gradlew" ]; then
        echo -e "${GREEN}✅ Gradle wrapper found${NC}"
    else
        echo -e "${RED}❌ Gradle wrapper not found${NC}"
        echo -e "${YELLOW}💡 Make sure you're in the Noah Backup root directory${NC}"
        exit 1
    fi
    
    echo ""
}

# Create .env file from template
create_env_file() {
    echo -e "${BLUE}📝 Creating Configuration File${NC}"
    
    if [ -f "$ENV_FILE" ]; then
        local overwrite
        overwrite=$(prompt_yes_no "Configuration file exists. Overwrite?" "n")
        if [ "$overwrite" != "y" ]; then
            echo -e "${YELLOW}⚠️  Keeping existing configuration${NC}"
            return
        fi
        cp "$ENV_FILE" "$ENV_FILE.backup"
        echo -e "${BLUE}💾 Backed up existing config to $ENV_FILE.backup${NC}"
    fi
    
    if [ ! -f "$ENV_EXAMPLE" ]; then
        echo -e "${RED}❌ Template file $ENV_EXAMPLE not found${NC}"
        exit 1
    fi
    
    cp "$ENV_EXAMPLE" "$ENV_FILE"
    echo -e "${GREEN}✅ Created $ENV_FILE from template${NC}"
    echo ""
}

# Configure basic settings
configure_basic_settings() {
    echo -e "${BLUE}⚙️  Basic Configuration${NC}"
    
    # Environment
    local env
    echo "Select environment:"
    echo "1) Development (with test settings)"
    echo "2) Production (with security hardening)"
    echo "3) Custom"
    
    while true; do
        echo -ne "${YELLOW}Choose environment [1-3]${NC}: "
        read env_choice
        case $env_choice in
            1)
                env="development"
                spring_profile="development"
                break
                ;;
            2)
                env="production"
                spring_profile="production"
                break
                ;;
            3)
                env=$(prompt_input "Environment name" "staging")
                spring_profile=$(prompt_input "Spring profile" "$env")
                break
                ;;
            *)
                echo -e "${RED}Please choose 1, 2, or 3${NC}"
                ;;
        esac
    done
    
    # Port
    local port
    port=$(prompt_input "Application port" "8080")
    
    # Update .env file
    sed -i "s/NOAH_ENV=.*/NOAH_ENV=$env/" "$ENV_FILE"
    sed -i "s/PORT=.*/PORT=$port/" "$ENV_FILE"
    sed -i "s/SPRING_PROFILES_ACTIVE=.*/SPRING_PROFILES_ACTIVE=$spring_profile/" "$ENV_FILE"
    
    echo -e "${GREEN}✅ Basic settings configured${NC}"
    echo ""
}

# Configure JWT settings
configure_jwt_settings() {
    echo -e "${BLUE}🔐 JWT Authentication Configuration${NC}"
    
    local generate_jwt
    generate_jwt=$(prompt_yes_no "Generate a new JWT secret?" "y")
    
    if [ "$generate_jwt" == "y" ]; then
        if command -v openssl &> /dev/null; then
            local jwt_secret
            jwt_secret=$(openssl rand -base64 64 | tr -d '\n')
            sed -i "s|JWT_SECRET=.*|JWT_SECRET=$jwt_secret|" "$ENV_FILE"
            echo -e "${GREEN}✅ Generated new JWT secret${NC}"
        else
            echo -e "${YELLOW}⚠️  OpenSSL not found. Please manually generate a JWT secret${NC}"
            local manual_jwt
            manual_jwt=$(prompt_input "Enter JWT secret (min 32 chars)" "" "true")
            if [ ${#manual_jwt} -ge 32 ]; then
                sed -i "s|JWT_SECRET=.*|JWT_SECRET=$manual_jwt|" "$ENV_FILE"
                echo -e "${GREEN}✅ JWT secret set${NC}"
            else
                echo -e "${RED}❌ JWT secret too short (minimum 32 characters)${NC}"
            fi
        fi
    fi
    
    echo ""
}

# Configure S3 settings
configure_s3_settings() {
    echo -e "${BLUE}☁️  S3 Storage Configuration${NC}"
    
    echo "Select S3 provider:"
    echo "1) AWS S3"
    echo "2) MinIO (self-hosted)"
    echo "3) AWS Lightsail Object Storage"
    echo "4) Custom S3-compatible"
    
    local s3_provider
    while true; do
        echo -ne "${YELLOW}Choose S3 provider [1-4]${NC}: "
        read s3_choice
        case $s3_choice in
            1)
                s3_provider="aws"
                break
                ;;
            2)
                s3_provider="minio"
                break
                ;;
            3)
                s3_provider="lightsail"
                break
                ;;
            4)
                s3_provider="custom"
                break
                ;;
            *)
                echo -e "${RED}Please choose 1, 2, 3, or 4${NC}"
                ;;
        esac
    done
    
    # Get S3 credentials
    local access_key
    local secret_key
    local region
    local bucket
    local endpoint=""
    local path_style="false"
    
    access_key=$(prompt_input "S3 Access Key ID" "" "true")
    secret_key=$(prompt_input "S3 Secret Access Key" "" "true")
    
    case $s3_provider in
        "aws")
            region=$(prompt_input "AWS Region" "us-east-1")
            bucket=$(prompt_input "S3 Bucket Name" "noah-backup-storage")
            ;;
        "minio")
            region=$(prompt_input "MinIO Region" "us-east-1")
            bucket=$(prompt_input "MinIO Bucket Name" "noah-backup")
            endpoint=$(prompt_input "MinIO Endpoint" "http://localhost:9000")
            path_style="true"
            ;;
        "lightsail")
            region=$(prompt_input "Lightsail Region" "us-east-1")
            bucket=$(prompt_input "Lightsail Bucket Name" "noah-backup-lightsail")
            endpoint="https://storage.$region.amazonaws.com"
            ;;
        "custom")
            region=$(prompt_input "Region" "us-east-1")
            bucket=$(prompt_input "Bucket Name" "noah-backup")
            endpoint=$(prompt_input "S3 Endpoint URL" "")
            local use_path_style
            use_path_style=$(prompt_yes_no "Use path-style access?" "n")
            [ "$use_path_style" == "y" ] && path_style="true"
            ;;
    esac
    
    # Update .env file
    sed -i "s|AWS_ACCESS_KEY_ID=.*|AWS_ACCESS_KEY_ID=$access_key|" "$ENV_FILE"
    sed -i "s|AWS_SECRET_ACCESS_KEY=.*|AWS_SECRET_ACCESS_KEY=$secret_key|" "$ENV_FILE"
    sed -i "s|AWS_REGION=.*|AWS_REGION=$region|" "$ENV_FILE"
    sed -i "s|S3_BUCKET_NAME=.*|S3_BUCKET_NAME=$bucket|" "$ENV_FILE"
    
    if [ -n "$endpoint" ]; then
        sed -i "s|S3_ENDPOINT=.*|S3_ENDPOINT=$endpoint|" "$ENV_FILE"
    fi
    
    sed -i "s|S3_PATH_STYLE_ACCESS=.*|S3_PATH_STYLE_ACCESS=$path_style|" "$ENV_FILE"
    
    echo -e "${GREEN}✅ S3 storage configured${NC}"
    echo ""
}

# Configure backup paths
configure_backup_paths() {
    echo -e "${BLUE}📂 Backup Paths Configuration${NC}"
    
    local os_type
    case "$(uname -s)" in
        CYGWIN*|MINGW*|MSYS*)
            os_type="windows"
            ;;
        *)
            os_type="unix"
            ;;
    esac
    
    echo "Configure backup paths for your system:"
    
    local paths=""
    local add_more="y"
    
    if [ "$os_type" == "windows" ]; then
        echo -e "${CYAN}💡 Windows path examples:${NC}"
        echo "  C:\\Users\\%USERNAME%\\Documents"
        echo "  C:\\Users\\%USERNAME%\\Desktop"
        echo "  C:\\ProgramData\\MyApp"
    else
        echo -e "${CYAN}💡 Unix path examples:${NC}"
        echo "  /home/\$USER/Documents"
        echo "  /home/\$USER/Pictures"
        echo "  /opt/myapp/data"
    fi
    
    while [ "$add_more" == "y" ]; do
        local path
        path=$(prompt_input "Enter backup path" "")
        
        if [ -n "$path" ]; then
            if [ -z "$paths" ]; then
                paths="$path"
            else
                paths="$paths;$path"
            fi
            echo -e "${GREEN}✅ Added: $path${NC}"
        fi
        
        add_more=$(prompt_yes_no "Add another path?" "n")
    done
    
    if [ -n "$paths" ]; then
        sed -i "s|NOAH_BACKUP_PATHS=.*|NOAH_BACKUP_PATHS=$paths|" "$ENV_FILE"
        echo -e "${GREEN}✅ Backup paths configured${NC}"
    else
        echo -e "${YELLOW}⚠️  No backup paths configured - you can add them later${NC}"
    fi
    
    echo ""
}

# Configure notifications (optional)
configure_notifications() {
    echo -e "${BLUE}📧 Notification Configuration (Optional)${NC}"
    
    local setup_notifications
    setup_notifications=$(prompt_yes_no "Configure notifications?" "n")
    
    if [ "$setup_notifications" != "y" ]; then
        echo -e "${BLUE}⏭️  Skipping notification setup${NC}"
        echo ""
        return
    fi
    
    # Email notifications
    local setup_email
    setup_email=$(prompt_yes_no "Setup email notifications?" "n")
    
    if [ "$setup_email" == "y" ]; then
        local smtp_host
        local smtp_port
        local smtp_user
        local smtp_pass
        local email_from
        local email_to
        
        smtp_host=$(prompt_input "SMTP Host" "smtp.gmail.com")
        smtp_port=$(prompt_input "SMTP Port" "587")
        smtp_user=$(prompt_input "SMTP Username" "")
        smtp_pass=$(prompt_input "SMTP Password/App Password" "" "true")
        email_from=$(prompt_input "From Email Address" "$smtp_user")
        email_to=$(prompt_input "To Email Address" "$smtp_user")
        
        sed -i "s|NOAH_NOTIFICATIONS_EMAIL_ENABLED=.*|NOAH_NOTIFICATIONS_EMAIL_ENABLED=true|" "$ENV_FILE"
        sed -i "s|SMTP_HOST=.*|SMTP_HOST=$smtp_host|" "$ENV_FILE"
        sed -i "s|SMTP_PORT=.*|SMTP_PORT=$smtp_port|" "$ENV_FILE"
        sed -i "s|SMTP_USERNAME=.*|SMTP_USERNAME=$smtp_user|" "$ENV_FILE"
        sed -i "s|SMTP_PASSWORD=.*|SMTP_PASSWORD=$smtp_pass|" "$ENV_FILE"
        sed -i "s|EMAIL_FROM=.*|EMAIL_FROM=$email_from|" "$ENV_FILE"
        sed -i "s|EMAIL_TO=.*|EMAIL_TO=$email_to|" "$ENV_FILE"
        
        echo -e "${GREEN}✅ Email notifications configured${NC}"
    fi
    
    # Slack notifications
    local setup_slack
    setup_slack=$(prompt_yes_no "Setup Slack notifications?" "n")
    
    if [ "$setup_slack" == "y" ]; then
        local slack_webhook
        local slack_channel
        
        slack_webhook=$(prompt_input "Slack Webhook URL" "")
        slack_channel=$(prompt_input "Slack Channel" "#backups")
        
        sed -i "s|NOAH_NOTIFICATIONS_SLACK_ENABLED=.*|NOAH_NOTIFICATIONS_SLACK_ENABLED=true|" "$ENV_FILE"
        sed -i "s|SLACK_WEBHOOK_URL=.*|SLACK_WEBHOOK_URL=$slack_webhook|" "$ENV_FILE"
        sed -i "s|SLACK_CHANNEL=.*|SLACK_CHANNEL=$slack_channel|" "$ENV_FILE"
        
        echo -e "${GREEN}✅ Slack notifications configured${NC}"
    fi
    
    echo ""
}

# Main setup function
main() {
    echo -e "${CYAN}This setup will guide you through configuring Noah Backup.${NC}"
    echo -e "${CYAN}You can always edit the .env file manually later.${NC}"
    echo ""
    
    local continue_setup
    continue_setup=$(prompt_yes_no "Continue with setup?" "y")
    
    if [ "$continue_setup" != "y" ]; then
        echo -e "${YELLOW}Setup cancelled.${NC}"
        exit 0
    fi
    
    check_prerequisites
    create_env_file
    configure_basic_settings
    configure_jwt_settings
    configure_s3_settings
    configure_backup_paths
    configure_notifications
    
    echo -e "${GREEN}🎉 Setup Complete!${NC}"
    echo "=============================================="
    echo ""
    echo -e "${BLUE}Next Steps:${NC}"
    echo "1. Validate configuration: ./scripts/validate-config.sh"
    echo "2. Start the application: ./gradlew bootRun"
    echo "3. Check logs in ./logs/ directory"
    echo "4. Access API documentation at: http://localhost:$PORT/swagger-ui.html"
    echo ""
    echo -e "${CYAN}Configuration saved to: $ENV_FILE${NC}"
    echo -e "${CYAN}Need help? Check the README.md or run: ./scripts/validate-config.sh --help${NC}"
    
    # Ask if user wants to validate now
    local validate_now
    validate_now=$(prompt_yes_no "Validate configuration now?" "y")
    
    if [ "$validate_now" == "y" ]; then
        echo ""
        echo -e "${BLUE}🔍 Running configuration validation...${NC}"
        ./scripts/validate-config.sh
    fi
}

# Parse command line arguments
case "$1" in
    -h|--help)
        echo "Noah Backup Easy Setup Script"
        echo ""
        echo "Usage: $0 [options]"
        echo ""
        echo "Options:"
        echo "  -h, --help     Show this help message"
        echo ""
        echo "This interactive script will guide you through:"
        echo "• Basic application configuration"
        echo "• JWT secret generation"
        echo "• S3 storage setup (AWS/MinIO/Lightsail)"
        echo "• Backup path configuration"
        echo "• Optional notification setup"
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac