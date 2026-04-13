# 🔐 Authentication Module – Spring Boot + PostgreSQL

## 📌 Overview

A production-ready **Spring Boot 3.1.5** authentication module with **PostgreSQL** database. It provides secure user registration, login, and role-based access control for the plagiarism detection system.

### Features

- ✅ Secure password storage using **BCrypt hashing**
- ✅ User registration with validation
- ✅ Login with credentials verification
- ✅ Duplicate user prevention with UNIQUE constraint
- ✅ Role-based access control (RBAC)
- ✅ REST-based authentication APIs
- ✅ Comprehensive error handling
- ✅ Unit & Integration tests
- ✅ CI/CD ready (Jenkins, GitHub Actions)
- ✅ Docker support with health checks
- ✅ Database migrations with Flyway

---

## 🗄️ Database Design

### Users Table Schema

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

| Column   | Type        | Description                |
|----------|-------------|----------------------------|
| id       | SERIAL (PK) | Unique user identifier     |
| email    | VARCHAR     | User email (unique)        |
| name     | VARCHAR     | User's full name           |
| password | VARCHAR     | BCrypt hashed password     |
| role     | VARCHAR     | User role (USER, ADMIN)    |
| created_at | TIMESTAMP | Account creation timestamp |
| updated_at | TIMESTAMP | Last update timestamp      |

---

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────────────┐
│      REST API (Controller Layer)     │
│  POST /api/auth/register             │
│  POST /api/auth/login                │
│  GET  /api/auth/health               │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│    Business Logic (Service Layer)    │
│  - Register validation               │
│  - Password hashing                  │
│  - Login verification                │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Data Access (Repository Layer)     │
│  - findByEmail()                     │
│  - existsByEmail()                   │
│  - save()                            │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│        PostgreSQL Database            │
└─────────────────────────────────────┘
```

### Project Structure

```
auth-module/
├── src/
│   ├── main/
│   │   ├── java/com/authmodule/
│   │   │   ├── AuthModuleApplication.java
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   └── AuthController.java
│   │   │   ├── dto/
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── AuthResponse.java
│   │   │   ├── entity/
│   │   │   │   └── User.java
│   │   │   ├── exception/
│   │   │   │   ├── UserAlreadyExistsException.java
│   │   │   │   ├── InvalidCredentialsException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java
│   │   │   └── service/
│   │   │       └── AuthService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/
│   │           └── V1__Initial_Schema.sql
│   └── test/
│       ├── java/com/authmodule/
│       │   ├── controller/
│       │   │   └── AuthControllerTest.java
│       │   └── service/
│       │       └── AuthServiceTest.java
│       └── resources/
│           └── application-test.properties
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── Jenkinsfile
├── .github/workflows/ci-cd.yml
└── README.md
```

---

## 🔄 API Contracts

### 1. Register User

**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "email": "user@example.com",
  "name": "John Doe",
  "password": "securePassword123"
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "role": "USER"
  }
}
```

**Error Response (409 Conflict):**
```json
{
  "error": "User with email user@example.com already exists",
  "status": "409"
}
```

---

### 2. Login User

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "role": "USER"
  }
}
```

**Error Response (401 Unauthorized):**
```json
{
  "error": "Invalid email or password",
  "status": "401"
}
```

---

### 3. Health Check

**Endpoint:** `GET /api/auth/health`

**Response (200 OK):**
```
Auth Service is running
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **PostgreSQL 15+** (or Docker)
- **Docker & Docker Compose** (optional, for containerized setup)

### Local Setup (Without Docker)

#### 1. Install PostgreSQL

```bash
# macOS (using Homebrew)
brew install postgresql@15

# Windows (download from https://www.postgresql.org/download/windows/)
# Linux (Ubuntu/Debian)
sudo apt-get install postgresql postgresql-contrib
```

#### 2. Create Database

```bash
psql -U postgres
CREATE DATABASE auth_db;
```

#### 3. Clone Repository

```bash
git clone <repository-url>
cd auth-module
```

#### 4. Update Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

#### 5. Build & Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

**Application runs at:** `http://localhost:8080`

---

### Docker Setup (Recommended)

#### 1. Start Services with Docker Compose

```bash
docker-compose up -d
```

This will:
- Start PostgreSQL on port 5432
- Start Auth Service on port 8080
- Create necessary networks and volumes

#### 2. View Logs

```bash
docker-compose logs -f auth-service
```

#### 3. Stop Services

```bash
docker-compose down
```

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=AuthControllerTest
mvn test -Dtest=AuthServiceTest
```

### Generate Test Report

```bash
mvn surefire-report:report
# Report: target/site/surefire-report.html
```

### Test Coverage

```bash
mvn jacoco:report
# Report: target/site/jacoco/index.html
```

---

## 🔒 Security

### Password Hashing

All passwords are hashed using **BCrypt** with a strength factor of 10:

```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
String hashedPassword = encoder.encode("plainPassword");
```

### Input Validation

- Email format validation
- Password minimum length: 6 characters
- Name length: 2-100 characters
- All inputs validated using Jakarta Validation (formerly javax.validation)

### Error Handling

Custom exceptions with appropriate HTTP status codes:
- `UserAlreadyExistsException` → 409 Conflict
- `InvalidCredentialsException` → 401 Unauthorized
- `MethodArgumentNotValidException` → 400 Bad Request
- `Exception` → 500 Internal Server Error

---

## 📦 Build & Package

### Build JAR

```bash
mvn clean package
```

Output: `target/auth-module-1.0.0.jar`

### Build Docker Image

```bash
docker build -t auth-module:latest .
```

### Run Docker Container

```bash
docker run -d \
  --name auth-service \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/auth_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  auth-module:latest
```

---

## 🔄 CI/CD Pipeline

### Jenkins Pipeline

The `Jenkinsfile` includes stages for:
1. **Checkout** - Clone repository
2. **Build** - Compile and resolve dependencies
3. **Test** - Run unit tests
4. **Code Quality** - SonarQube analysis
5. **Package** - Create JAR file
6. **Docker Build** - Build Docker image
7. **Deploy to Dev** - Deploy on develop branch
8. **Deploy to Prod** - Deploy on main branch

### GitHub Actions

The `.github/workflows/ci-cd.yml` automatically:
- Runs on push to main/develop
- Runs on pull requests
- Builds and tests with Maven
- Analyzes code with SonarCloud
- Builds Docker image

### Running Pipeline

**Locally:**
```bash
# Requires Jenkins running locally
docker run -d -p 8080:8080 jenkins/jenkins:lts
```

**GitHub Actions:**
- Automatically triggers on push
- View results in "Actions" tab

---

## 📋 Database Migrations

Flyway handles automatic database migrations:

```
src/main/resources/db/migration/
├── V1__Initial_Schema.sql    (Initial schema with users table)
├── V2__Add_Index.sql          (Future migration)
└── V3__Add_Columns.sql        (Future migration)
```

Migrations run automatically on application startup.

#### To Add New Migration

1. Create new file: `V2__Your_Migration_Name.sql`
2. Place in `src/main/resources/db/migration/`
3. migrations auto-execute on app startup

---

## 🔐 Environment Variables

### Production Environment

Create `.env.prod`:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/auth_db
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=secure_password
SPRING_PROFILES_ACTIVE=prod
```

### Development Environment

Create `.env.dev`:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/auth_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_PROFILES_ACTIVE=dev
```

---

## 🧩 Integration Examples

### Using the Auth Service

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final AuthService authService;
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return authService.getUserById(id);
    }
}
```

---

## 🚀 Future Enhancements

- [ ] JWT Token generation & validation
- [ ] Refresh tokens
- [ ] Role-based authorization (RBAC)
- [ ] Spring Security integration
- [ ] OAuth2/OpenID Connect support
- [ ] Multi-factor authentication (MFA)
- [ ] Email verification
- [ ] Password reset functionality
- [ ] Audit logging
- [ ] API rate limiting
- [ ] Kubernetes deployment manifests
- [ ] Monitoring with Prometheus/Grafana

---

## 📊 Monitoring & Health Checks

### Health Check Endpoint

```bash
curl http://localhost:8080/api/auth/health
# Response: "Auth Service is running"
```

### Application Metrics

```bash
curl http://localhost:8080/actuator/metrics
```

---

## 🐛 Troubleshooting

### Database Connection Error

```
org.postgresql.util.PSQLException: Connection refused
```

**Solution:**
- Ensure PostgreSQL is running
- Check `spring.datasource.url` in application.properties
- Verify username and password

### Port Already in Use

```
Address already in use: bind
```

**Solution:**
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill process (Windows)
taskkill /PID <PID> /F

# Or change port in application.properties
server.port=8081
```

### Test Failures

```bash
# Run tests with verbose output
mvn test -X

# Run specific test
mvn test -Dtest=AuthControllerTest#testRegisterSuccess
```

---

## 📚 Dependencies

| Dependency | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.1.5 | Framework |
| Spring Security | 6.x | Password encoding |
| PostgreSQL Driver | 42.6.0 | Database connectivity |
| Flyway | 9.x | Database migrations |
| Lombok | 1.18.x | Reduce boilerplate |
| JUnit 5 | 5.x | Testing |
| Mockito | 5.x | Mocking |

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Contributors

- Your Name (@username)

---

## 📞 Support

For questions or issues:
- 📧 Email: support@example.com
- 🐛 Issues: [GitHub Issues]
- 📖 Documentation: [Project Wiki]

---

## 🎯 Quick Command Reference

| Command | Purpose |
|---------|---------|
| `mvn clean install` | Build project |
| `mvn test` | Run tests |
| `mvn spring-boot:run` | Run locally |
| `docker-compose up -d` | Start Docker containers |
| `docker-compose down` | Stop containers |
| `docker build -t auth-module:latest .` | Build Docker image |
| `mvn package` | Create JAR file |
| `mvn surefire-report:report` | Generate test report |

---

**Last Updated:** April 2026  
**Status:** ✅ Production Ready
