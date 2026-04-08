#!/bin/bash

# Plagiarism Detection System Setup Script
set -e

echo "========================================="
echo "Plagiarism Detection System Setup"
echo "========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

# Check prerequisites
echo "Checking prerequisites..."

# Check Java
if command -v java >/dev/null 2>&1; then
    java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    print_status "Java version: $java_version"
else
    print_error "Java is not installed. Please install Java 17 or later."
    exit 1
fi

# Check Maven
if command -v mvn >/dev/null 2>&1; then
    mvn_version=$(mvn -version | head -n 1 | awk '{print $3}')
    print_status "Maven version: $mvn_version"
else
    print_error "Maven is not installed. Please install Maven 3.8 or later."
    exit 1
fi

# Check Docker
if command -v docker >/dev/null 2>&1; then
    docker_version=$(docker --version | awk '{print $3}' | sed 's/,//')
    print_status "Docker version: $docker_version"
else
    print_error "Docker is not installed. Please install Docker."
    exit 1
fi

# Check Docker Compose
if command -v docker-compose >/dev/null 2>&1; then
    compose_version=$(docker-compose --version | awk '{print $3}' | sed 's/,//')
    print_status "Docker Compose version: $compose_version"
else
    print_error "Docker Compose is not installed. Please install Docker Compose."
    exit 1
fi

# Create directory structure
echo -e "\nCreating directory structure..."
mkdir -p storage/{uploads,extracted,reports}
mkdir -p logs
mkdir -p jplag
mkdir -p backups
print_status "Directory structure created"

# Download JPlag if not present
echo -e "\nChecking JPlag..."
if [ ! -f jplag/jplag.jar ]; then
    print_warning "JPlag not found. Downloading..."
    curl -L -o jplag/jplag.jar https://github.com/jplag/jplag/releases/download/v3.0.0/jplag-3.0.0-jar-with-dependencies.jar
    if [ $? -eq 0 ]; then
        print_status "JPlag downloaded successfully"
    else
        print_error "Failed to download JPlag"
        exit 1
    fi
else
    print_status "JPlag already exists"
fi

# Setup environment variables
echo -e "\nSetting up environment variables..."
if [ ! -f .env ]; then
    cp .env.example .env
    print_status "Created .env file from template"
    print_warning "Please update .env file with your configuration"
else
    print_status ".env file already exists"
fi

# Build the application
echo -e "\nBuilding application..."
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    print_status "Application built successfully"
else
    print_error "Build failed"
    exit 1
fi

# Start Docker services
echo -e "\nStarting Docker services..."
cd docker
docker-compose up -d mongodb mongo-express

# Wait for MongoDB
echo "Waiting for MongoDB to be ready..."
sleep 10

# Check MongoDB
if docker exec plagiarism-mongodb mongosh --eval "db.runCommand('ping')" >/dev/null 2>&1; then
    print_status "MongoDB is ready"
else
    print_error "MongoDB connection failed"
    exit 1
fi

# Start application
docker-compose up -d app
print_status "Application started"

# Start monitoring
docker-compose up -d prometheus grafana
print_status "Monitoring services started"

cd ..

echo -e "\n========================================="
echo -e "${GREEN}Setup Complete!${NC}"
echo "========================================="
echo ""
echo "Access URLs:"
echo "  Application: http://localhost:8080"
echo "  MongoDB Express: http://localhost:8081 (admin/admin123)"
echo "  Grafana: http://localhost:3000 (admin/admin)"
echo "  Prometheus: http://localhost:9090"
echo ""
echo "API Endpoints:"
echo "  POST /auth/register - Register"
echo "  POST /auth/login - Login"
echo "  POST /api/jobs/upload - Upload submissions"
echo "  GET /api/jobs/{id}/status - Check status"
echo ""
echo "To view logs: docker-compose -f docker/docker-compose.yml logs -f app"
echo "To stop: docker-compose -f docker/docker-compose.yml down"
echo ""