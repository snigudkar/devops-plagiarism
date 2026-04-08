#!/bin/bash

# Deployment script
set -e

ENVIRONMENT=${1:-staging}
VERSION=${2:-latest}

echo "========================================="
echo "Deploying to $ENVIRONMENT environment"
echo "Version: $VERSION"
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

# Load environment variables
if [ -f .env ]; then
    source .env
    print_status "Loaded environment variables"
else
    print_error ".env file not found"
    exit 1
fi

# Build with appropriate profile
echo -e "\nBuilding application..."
if [ "$ENVIRONMENT" = "production" ]; then
    mvn clean package -Dspring.profiles.active=prod -DskipTests
else
    mvn clean package -Dspring.profiles.active=dev -DskipTests
fi

if [ $? -eq 0 ]; then
    print_status "Application built successfully"
else
    print_error "Build failed"
    exit 1
fi

# Build Docker image
echo -e "\nBuilding Docker image..."
docker build -t plagiarism-system:$VERSION -f docker/Dockerfile .

# Tag image
docker tag plagiarism-system:$VERSION plagiarism-system:$ENVIRONMENT

# Deploy with Docker Compose
echo -e "\nDeploying to $ENVIRONMENT..."
cd docker

if [ "$ENVIRONMENT" = "production" ]; then
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
else
    docker-compose up -d
fi

# Wait for application to start
echo "Waiting for application to start..."
sleep 15

# Run smoke tests
echo -e "\nRunning smoke tests..."
if curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
    print_status "Application is healthy"
else
    print_error "Application health check failed"
    exit 1
fi

cd ..

echo -e "\n========================================="
echo -e "${GREEN}Deployment to $ENVIRONMENT completed successfully!${NC}"
echo "========================================="
echo "Application URL: http://localhost:8080"
echo "Version: $VERSION"
echo ""