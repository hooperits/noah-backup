#!/bin/sh

# MinIO Setup Script for Noah Backup
# This script creates the necessary buckets and policies

set -e

echo "🗄️  Setting up MinIO for Noah Backup..."

# Wait for MinIO to be ready
echo "Waiting for MinIO to be ready..."
until mc alias set noah-minio "$MINIO_ENDPOINT" "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD"; do
    echo "MinIO not ready, waiting..."
    sleep 5
done

echo "✅ Connected to MinIO"

# Create bucket if it doesn't exist
if mc ls noah-minio/"$BUCKET_NAME" > /dev/null 2>&1; then
    echo "✅ Bucket '$BUCKET_NAME' already exists"
else
    echo "📦 Creating bucket '$BUCKET_NAME'..."
    mc mb noah-minio/"$BUCKET_NAME"
    echo "✅ Bucket '$BUCKET_NAME' created"
fi

# Set bucket policy (optional - for development)
echo "🔐 Setting bucket policy..."
cat > /tmp/policy.json << EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Principal": {
                "AWS": ["*"]
            },
            "Action": ["s3:GetBucketLocation"],
            "Resource": ["arn:aws:s3:::$BUCKET_NAME"]
        },
        {
            "Effect": "Allow",
            "Principal": {
                "AWS": ["*"]
            },
            "Action": ["s3:ListBucket"],
            "Resource": ["arn:aws:s3:::$BUCKET_NAME"],
            "Condition": {
                "StringEquals": {
                    "s3:prefix": ["backups/"]
                }
            }
        },
        {
            "Effect": "Allow",
            "Principal": {
                "AWS": ["*"]
            },
            "Action": ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"],
            "Resource": ["arn:aws:s3:::$BUCKET_NAME/backups/*"]
        }
    ]
}
EOF

mc policy set-json /tmp/policy.json noah-minio/"$BUCKET_NAME"
echo "✅ Bucket policy configured"

# Enable versioning
echo "🔄 Enabling versioning..."
mc version enable noah-minio/"$BUCKET_NAME"
echo "✅ Versioning enabled"

# Create lifecycle policy for cleanup (optional)
echo "🧹 Setting up lifecycle policy..."
cat > /tmp/lifecycle.json << EOF
{
    "Rules": [
        {
            "ID": "noah-backup-retention",
            "Status": "Enabled",
            "Filter": {
                "Prefix": "backups/"
            },
            "Expiration": {
                "Days": 90
            },
            "NoncurrentVersionExpiration": {
                "NoncurrentDays": 30
            }
        }
    ]
}
EOF

mc ilm set --config /tmp/lifecycle.json noah-minio/"$BUCKET_NAME"
echo "✅ Lifecycle policy configured (90-day retention)"

# Create test folder structure
echo "📁 Creating folder structure..."
echo "Noah Backup Test File - $(date)" | mc pipe noah-minio/"$BUCKET_NAME"/backups/.noah-backup-initialized
echo "✅ Folder structure created"

echo ""
echo "🎉 MinIO setup complete!"
echo "📊 MinIO Console: http://localhost:9001"
echo "🗄️  Bucket: $BUCKET_NAME"
echo "🔑 Access Key: $MINIO_ROOT_USER"
echo "🔐 Secret Key: $MINIO_ROOT_PASSWORD"
echo ""