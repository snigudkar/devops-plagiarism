#!/bin/bash

# Quick Start Script for Auth Module

set -e

echo "================================"
echo "🔐 Auth Module - Quick Start"
echo "================================"
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    echo "   Download from: https://www.docker.com/products/docker-desktop"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed."
    exit 1
fi

echo "✅ Docker and Docker Compose found"
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java 17+ is not installed."
    exit 1
fi

echo "✅ Java found"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed."
    exit 1
fi

echo "✅ Maven found"
echo ""

echo "🔨 Building application..."
mvn clean install

echo ""
echo "🐳 Starting Docker containers..."
docker-compose up -d

echo ""
echo "⏳ Waiting for services to start..."
sleep 10

echo ""
echo "✅ Auth Module is running!"
echo ""
echo "📍 Service URL: http://localhost:8080"
echo "📍 Database: postgres://localhost:5432/auth_db"
echo ""
echo "🧪 Test the API:"
echo "   POST /api/auth/register"
echo "   POST /api/auth/login"
echo "   GET  /api/auth/health"
echo ""
echo "📊 View logs: docker-compose logs -f auth-service"
echo "🛑 Stop services: docker-compose down"
echo ""
