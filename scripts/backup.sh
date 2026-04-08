#!/bin/bash

# Backup script for Plagiarism Detection System
set -e

BACKUP_DIR="./backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RETENTION_DAYS=7

echo "========================================="
echo "Starting Backup - $TIMESTAMP"
echo "========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_status() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

# Create backup directory
mkdir -p $BACKUP_DIR
print_status "Backup directory created: $BACKUP_DIR"

# Backup MongoDB
echo -e "\nBacking up MongoDB..."
if docker exec plagiarism-mongodb mongodump \
    --username admin \
    --password admin123 \
    --authenticationDatabase admin \
    --db plagiarism_db \
    --out /tmp/backup >/dev/null 2>&1; then
    
    docker cp plagiarism-mongodb:/tmp/backup/plagiarism_db $BACKUP_DIR/mongodb_$TIMESTAMP
    docker exec plagiarism-mongodb rm -rf /tmp/backup
    
    # Compress MongoDB backup
    tar -czf $BACKUP_DIR/mongodb_$TIMESTAMP.tar.gz -C $BACKUP_DIR mongodb_$TIMESTAMP
    rm -rf $BACKUP_DIR/mongodb_$TIMESTAMP
    
    print_status "MongoDB backup completed: mongodb_$TIMESTAMP.tar.gz"
else
    print_error "MongoDB backup failed"
fi

# Backup storage files
echo -e "\nBacking up storage files..."
if [ -d "storage" ]; then
    tar -czf $BACKUP_DIR/storage_$TIMESTAMP.tar.gz storage/
    print_status "Storage backup completed: storage_$TIMESTAMP.tar.gz"
else
    print_warning "Storage directory not found"
fi

# Backup logs
echo -e "\nBacking up logs..."
if [ -d "logs" ]; then
    tar -czf $BACKUP_DIR/logs_$TIMESTAMP.tar.gz logs/
    print_status "Logs backup completed: logs_$TIMESTAMP.tar.gz"
else
    print_warning "Logs directory not found"
fi

# Backup configuration
echo -e "\nBacking up configuration files..."
tar -czf $BACKUP_DIR/config_$TIMESTAMP.tar.gz \
    .env \
    docker/docker-compose.yml \
    docker/prometheus.yml \
    src/main/resources/application.yml \
    src/main/resources/application-prod.yml 2>/dev/null || true
print_status "Configuration backup completed"

# Create backup manifest
echo -e "\nCreating backup manifest..."
cat > $BACKUP_DIR/manifest_$TIMESTAMP.txt << EOF
Backup Manifest
===============
Date: $(date)
Timestamp: $TIMESTAMP

Contents:
- MongoDB Database Dump
- Storage Files
- Log Files
- Configuration Files

Applications:
- Plagiarism Detection System
- MongoDB
- Prometheus
- Grafana
EOF
print_status "Backup manifest created"

# Clean old backups
echo -e "\nCleaning backups older than $RETENTION_DAYS days..."
find $BACKUP_DIR -type f -name "*.tar.gz" -mtime +$RETENTION_DAYS -delete
find $BACKUP_DIR -type f -name "*.txt" -mtime +$RETENTION_DAYS -delete
print_status "Old backups cleaned"

# Calculate backup size
BACKUP_SIZE=$(du -sh $BACKUP_DIR | awk '{print $1}')

echo -e "\n========================================="
echo -e "${GREEN}Backup Completed Successfully!${NC}"
echo "========================================="
echo "Backup Location: $BACKUP_DIR"
echo "Backup Size: $BACKUP_SIZE"
echo "Timestamp: $TIMESTAMP"
echo ""
echo "Backup files:"
ls -lh $BACKUP_DIR | grep $TIMESTAMP