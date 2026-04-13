@echo off
REM Quick Start Script for Auth Module (Windows)

echo ================================
echo Testing Auth Module - Quick Start
echo ================================
echo.

REM Check if Docker is installed
docker --version > nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not installed. Please install Docker first.
    echo Download from: https://www.docker.com/products/docker-desktop
    exit /b 1
)

echo [OK] Docker found
echo.

REM Check if java is installed
java -version > nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java 17+ is not installed.
    exit /b 1
)

echo [OK] Java found
echo.

REM Check if mvn is installed
mvn --version > nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed.
    exit /b 1
)

echo [OK] Maven found
echo.

echo Building application...
call mvn clean install

echo.
echo Starting Docker containers...
docker-compose up -d

echo.
echo Waiting for services to start...
timeout /t 10

echo.
echo [OK] Auth Module is running!
echo.
echo Service URL: http://localhost:8080
echo Database: postgres://localhost:5432/auth_db
echo.
echo Test the API:
echo   POST /api/auth/register
echo   POST /api/auth/login
echo   GET  /api/auth/health
echo.
echo View logs: docker-compose logs -f auth-service
echo Stop services: docker-compose down
echo.
