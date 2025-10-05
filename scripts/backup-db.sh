#!/bin/bash

# Photo CRM SaaS - Database Backup Script
# This script creates a backup of the PostgreSQL database

set -e

# Configuration
DB_HOST=${DB_HOST:-"localhost"}
DB_PORT=${DB_PORT:-"5432"}
DB_NAME=${DB_NAME:-"photocrm_dev"}
DB_USER=${DB_USER:-"photocrm_user"}
BACKUP_DIR=${BACKUP_DIR:-"./backups"}
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/photocrm_backup_${TIMESTAMP}.sql"

echo "🗄️ Starting database backup..."

# Create backup directory if it doesn't exist
mkdir -p ${BACKUP_DIR}

# Check if pg_dump is available
if ! command -v pg_dump &> /dev/null; then
    echo "❌ pg_dump is not installed. Please install PostgreSQL client tools."
    exit 1
fi

# Create backup
echo "📦 Creating backup: ${BACKUP_FILE}"
pg_dump -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USER} -d ${DB_NAME} > ${BACKUP_FILE}

# Compress backup
echo "🗜️ Compressing backup..."
gzip ${BACKUP_FILE}
BACKUP_FILE="${BACKUP_FILE}.gz"

# Get backup size
BACKUP_SIZE=$(du -h ${BACKUP_FILE} | cut -f1)

echo "✅ Database backup completed successfully!"
echo "📁 Backup file: ${BACKUP_FILE}"
echo "📊 Backup size: ${BACKUP_SIZE}"

# Optional: Upload to S3 (uncomment and configure if needed)
# echo "☁️ Uploading backup to S3..."
# aws s3 cp ${BACKUP_FILE} s3://your-backup-bucket/database-backups/

# Optional: Clean up old backups (keep last 7 days)
echo "🧹 Cleaning up old backups..."
find ${BACKUP_DIR} -name "photocrm_backup_*.sql.gz" -mtime +7 -delete

echo "🎉 Backup process completed!"