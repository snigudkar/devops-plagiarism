# 🚀 Deployment Guide

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Local Development](#local-development)
- [Docker Deployment](#docker-deployment)
- [Jenkins CI/CD](#jenkins-cicd)
- [Production Deployment](#production-deployment)
- [Monitoring & Troubleshooting](#monitoring--troubleshooting)

---

## Prerequisites

### System Requirements

- **OS:** Linux, macOS, or Windows
- **Java:** 17 or higher
- **Maven:** 3.8+
- **Docker:** 20.10+ (for containerized deployment)
- **PostgreSQL:** 15+ (or use Docker)
- **Git:** 2.25+

### Software Installation

```bash
# Verify Java
java -version

# Verify Maven
mvn -version

# Verify Docker
docker --version

# Verify Git
git --version
```

---

## Local Development

### Quick Start (5 minutes)

#### Linux/macOS

```bash
# Clone repository
git clone https://github.com/org/auth-module.git
cd auth-module

# Make start script executable
chmod +x start.sh

# Run setup
./start.sh
```

#### Windows

```cmd
# Clone repository
git clone https://github.com/org/auth-module.git
cd auth-module

# Run setup
start.bat
```

### Manual Setup

```bash
# Build project
mvn clean install

# Start PostgreSQL (if not running)
docker-compose up -d postgres

# Run application
mvn spring-boot:run

# Check if running
curl http://localhost:8080/api/auth/health
```

---

## Docker Deployment

### 1. Build Docker Image

```bash
# Build image
docker build -t auth-module:1.0.0 .

# Tag as latest
docker tag auth-module:1.0.0 auth-module:latest

# List images
docker images | grep auth-module
```

### 2. Run Single Container

```bash
docker run -d \
  --name auth-service \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/auth_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SPRING_PROFILES_ACTIVE=dev \
  auth-module:latest

# View logs
docker logs -f auth-service

# Stop container
docker stop auth-service
```

### 3. Using Docker Compose

```bash
# Start all services
docker-compose up -d

# View status
docker-compose ps

# View logs
docker-compose logs -f auth-service

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### 4. Push to Docker Registry

```bash
# Tag for registry
docker tag auth-module:latest myregistry/auth-module:1.0.0

# Login to registry (Docker Hub example)
docker login

# Push image
docker push myregistry/auth-module:1.0.0
```

---

## Jenkins CI/CD

### 1. Jenkins Setup

#### Prerequisites
- Jenkins running (port 8000+)
- Docker installed on Jenkins agent
- Maven installed globally

#### Installation

```bash
# Using Docker
docker pull jenkins/jenkins:lts

docker run -d \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  --name jenkins \
  jenkins/jenkins:lts

# Access at http://localhost:8080
```

### 2. Create Jenkins Pipeline

1. **New Item** → **Pipeline**
2. **Definition:** Pipeline script from SCM
3. **SCM:** Git
4. **Repository URL:** `https://github.com/org/auth-module.git`
5. **Script Path:** `Jenkinsfile`

### 3. Configure Build Triggers

- [x] GitHub hook trigger for GITScm polling
- [x] Poll SCM: `H H * * *` (daily)

### 4. Run Pipeline

```bash
# Manual trigger from Jenkins UI
# Or automatic trigger on git push
git push origin feature/new-feature
```

### 5. View Pipeline Stages

- Checkout ✅
- Build ✅
- Test ✅
- Code Quality ✅
- Package ✅
- Build Docker Image ✅
- Deploy to Dev ✅
- Deploy to Prod ✅

---

## Production Deployment

### 1. Pre-Deployment Checklist

- [ ] All tests passing
- [ ] Code review approved
- [ ] Security scan completed
- [ ] Documentation updated
- [ ] Database backups created
- [ ] Rollback plan prepared

### 2. Database Migration

```bash
# Test migration locally first
mvn flyway:clean flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/auth_db_test

# Production migration (automatic on startup with validate mode)
# No manual action needed if ddl-auto=validate
```

### 3. Blue-Green Deployment

#### Setup

```bash
# Current (Blue) environment
docker run -d \
  --name auth-blue \
  -p 8001:8080 \
  auth-module:current

# New (Green) environment
docker run -d \
  --name auth-green \
  -p 8002:8080 \
  auth-module:new

# Test green environment
curl http://localhost:8002/api/auth/health

# Switch load balancer to green
# Update nginx/haproxy configuration

# Keep blue running for quick rollback
```

### 4. Rolling Deployment

```bash
# Deploy new version to one container at a time
for i in {1..3}; do
  # Stop old container
  docker stop auth-service-$i
  
  # Start new container
  docker run -d \
    --name auth-service-$i \
    -p 808$i:8080 \
    auth-module:new
  
  # Wait for health check
  sleep 10
done
```

### 5. Kubernetes Deployment

```bash
# Apply manifests
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/config.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# Check deployment
kubectl get deployments -n auth-module
kubectl get pods -n auth-module

# View logs
kubectl logs -f deployment/auth-module -n auth-module

# Scale deployment
kubectl scale deployment auth-module --replicas=3 -n auth-module
```

---

## Environment Configuration

### Development Environment

```bash
# application-dev.properties
SPRING_PROFILE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/auth_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

### Staging Environment

```bash
# application-staging.properties  
SPRING_PROFILE=staging
SPRING_DATASOURCE_URL=jdbc:postgresql://staging-db:5432/auth_db_staging
SPRING_DATASOURCE_USERNAME=staging_user
SPRING_DATASOURCE_PASSWORD=${STAGING_DB_PASSWORD}
```

### Production Environment

```bash
# application-prod.properties
SPRING_PROFILE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://${PROD_DB_HOST}:5432/auth_db
SPRING_DATASOURCE_USERNAME=${PROD_DB_USER}
SPRING_DATASOURCE_PASSWORD=${PROD_DB_PASSWORD}
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
```

---

## Health Checks & Monitoring

### Application Health

```bash
# Health check endpoint
curl http://localhost:8080/api/auth/health

# Detailed health (if Actuator enabled)
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics
```

### Docker Health Check

```bash
# Check container health
docker inspect --format='{{.State.Health.Status}}' auth-service

# View health check logs
docker inspect auth-service | grep -A 10 "Health"
```

### Database Connection

```bash
# Connect to PostgreSQL
psql -U postgres -d auth_db -h localhost

# Check tables
\dt

# Check user count
SELECT COUNT(*) FROM users;

# Exit
\q
```

---

## Monitoring & Troubleshooting

### Common Issues & Solutions

#### 1. Database Connection Error

```
Error: org.postgresql.util.PSQLException: Connection refused
```

**Solution:**
```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Check database exists
psql -U postgres -l | grep auth_db

# Check connection string
# Verify: host, port, database name, username, password
```

#### 2. Port Already in Use

```
Error: Address already in use: bind :::8080
```

**Solution:**
```bash
# Kill process using port 8080
# Linux/macOS
lsof -i :8080 | grep -v PID | awk '{print $2}' | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Or use different port
SPRING_SERVER_PORT=8081 mvn spring-boot:run
```

#### 3. Docker Permission Denied

```
Error: Got permission denied while trying to connect to Docker daemon
```

**Solution:**
```bash
# Add user to docker group
sudo usermod -aG docker $USER

# Apply new group
newgrp docker

# Or use sudo
sudo docker-compose up -d
```

#### 4. Flyway Migration Failed

```
Error: Flyway migration failed
```

**Solution:**
```bash
# Check migration files
ls src/main/resources/db/migration/

# Validate SQL syntax
cat src/main/resources/db/migration/V1__Initial_Schema.sql

# Reset migrations (dev only!)
mvn flyway:clean

# Or check logs
docker logs auth-service | grep -i migration
```

### Viewing Logs

```bash
# Application logs
docker logs -f auth-service

# Last 100 lines
docker logs --tail 100 auth-service

# Follow logs
docker logs -f auth-service

# Kubernetes logs
kubectl logs deployment/auth-module -n auth-module -f

# System logs (if running on host)
tail -f /var/log/auth-module/application.log
```

### Performance Monitoring

```bash
# CPU and Memory usage
docker stats auth-service

# Database query performance
# Enable query logging in application-prod.properties
# logging.level.org.hibernate.stat=DEBUG

# JVM metrics
# Enable in Actuator: management.endpoints.web.exposure.include=metrics
curl http://localhost:8080/actuator/metrics/
```

---

## Rollback Plan

### Quick Rollback

```bash
# If using Docker
docker stop auth-service-new
docker start auth-service-old

# If using Kubernetes
kubectl rollout undo deployment/auth-module -n auth-module

# If using Jenkins
# Manually trigger build with previous version tag
```

### Database Rollback

```bash
# Flyway rollback (not automatic, must have undo scripts)
mvn flyway:undo

# Manual rollback
psql -U postgres -d auth_db -f sql/rollback_v2.sql
```

---

## Performance Tuning

### Database Optimization

```properties
# Connection pooling
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# Batch operations
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

### JVM Tuning

```bash
# Memory allocation
java -Xms512m -Xmx2g -jar auth-module.jar

# Or in Docker
JAVA_OPTS="-Xms512m -Xmx2g"
```

---

## Security Checklist

- [ ] Environment variables secured (not in code)
- [ ] Database password rotated
- [ ] HTTPS/TLS enabled
- [ ] Firewall rules configured
- [ ] API rate limiting enabled
- [ ] Input validation in place
- [ ] Security headers configured
- [ ] Secrets in secure vault (AWS Secrets Manager, HashiCorp Vault)

---

## Backup & Recovery

### Database Backup

```bash
# Backup PostgreSQL
pg_dump -U postgres auth_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore from backup
psql -U postgres auth_db < backup_20240413_120000.sql

# Regular backups with cron
0 2 * * * pg_dump -U postgres auth_db > /backups/auth_db_$(date +\%Y\%m\%d).sql
```

---

## Maintenance

### Regular Maintenance

```bash
# Clean up old Docker images
docker image prune -a

# Clean up unused volumes
docker volume prune

# Update dependencies
mvn versions:display-dependency-updates

# Security updates
mvn dependency-check:check
```

---

## Support & Escalation

For deployment issues:
1. Check logs: `docker logs auth-service`
2. Verify configuration: `application-prod.properties`
3. Test database connectivity
4. Check system resources (disk, memory)
5. Contact DevOps team

---

**Ready to deploy? 🚀**
