# 📚 Project Structure & File Guide

## 📁 Complete Directory Structure

```
auth-module/
│
├── 📄 pom.xml                          # Maven configuration with dependencies
├── 📄 Jenkinsfile                      # Jenkins CI/CD pipeline definition
├── 📄 Dockerfile                       # Docker image configuration
├── 📄 docker-compose.yml               # Multi-container Docker setup
├── 📄 .gitignore                       # Git ignore patterns
│
├── 📋 Documentation Files
│   ├── README.md                       # Main project documentation
│   ├── DATABASE_SETUP.md               # Database setup instructions
│   ├── DEPLOYMENT.md                   # Deployment guide
│   ├── API_TESTING.md                  # API testing guide
│   ├── CONTRIBUTING.md                 # Contributing guidelines
│   └── PROJECT_STRUCTURE.md            # This file
│
├── 🚀 Setup Scripts
│   ├── start.sh                        # Linux/macOS quick start script
│   └── start.bat                       # Windows quick start script
│
├── 📁 src/main/java/com/authmodule/
│   │
│   ├── AuthModuleApplication.java      # Spring Boot entry point
│   │
│   ├── 📁 config/
│   │   └── SecurityConfig.java         # Spring Security configuration
│   │
│   ├── 📁 controller/
│   │   └── AuthController.java         # REST API endpoints
│   │                                     • POST /api/auth/register
│   │                                     • POST /api/auth/login
│   │                                     • GET /api/auth/health
│   │
│   ├── 📁 service/
│   │   └── AuthService.java            # Business logic
│   │                                     • User registration
│   │                                     • Login verification
│   │                                     • Password hashing
│   │
│   ├── 📁 repository/
│   │   └── UserRepository.java         # Data access layer (JPA)
│   │                                     • findByEmail()
│   │                                     • existsByEmail()
│   │
│   ├── 📁 entity/
│   │   └── User.java                   # JPA entity (users table)
│   │
│   ├── 📁 dto/
│   │   ├── RegisterRequest.java        # Registration request DTO
│   │   ├── LoginRequest.java           # Login request DTO
│   │   └── AuthResponse.java           # Auth response DTO
│   │
│   └── 📁 exception/
│       ├── UserAlreadyExistsException.java
│       ├── InvalidCredentialsException.java
│       └── GlobalExceptionHandler.java # Global error handling
│
├── 📁 src/main/resources/
│   ├── application.properties          # Default configuration
│   ├── application-dev.properties      # Development config
│   ├── application-staging.properties  # Staging config
│   ├── application-prod.properties     # Production config
│   │
│   └── 📁 db/migration/
│       └── V1__Initial_Schema.sql      # Database schema (Flyway)
│
├── 📁 src/test/java/com/authmodule/
│   ├── 📁 controller/
│   │   └── AuthControllerTest.java     # Controller tests
│   │                                     • 6 test cases
│   │                                     • Mocked service layer
│   │                                     • REST endpoint validation
│   │
│   └── 📁 service/
│       └── AuthServiceTest.java        # Service tests
│                                         • 7 test cases
│                                         • Mocked repository layer
│                                         • Business logic validation
│
├── 📁 src/test/resources/
│   └── application-test.properties     # Test configuration (H2 database)
│
├── 📁 .github/
│   └── 📁 workflows/
│       └── ci-cd.yml                   # GitHub Actions CI/CD pipeline
│
└── 📁 target/                          # Build output (generated)
    ├── auth-module-1.0.0.jar           # Executable JAR
    └── ...
```

---

## 📖 File Descriptions

### Pom.xml
**Purpose:** Maven configuration file
- Defines project metadata
- Manages dependencies
- Configures build plugins
- Spring Boot 3.1.5
- Java 17

### Jenkinsfile
**Purpose:** Jenkins CI/CD pipeline definition
- 8 stages: Checkout, Build, Test, Code Quality, Package, Docker Build, Deploy Dev, Deploy Prod
- Runs on any agent
- 30-minute timeout
- Post-build test reporting

### Docker Files
- **Dockerfile:** Multi-layer Docker image for production
- **docker-compose.yml:** Orchestrates PostgreSQL + Auth Service

### Documentation (Markdown)
| File | Purpose |
|------|---------|
| README.md | Complete project overview, features, architecture |
| DATABASE_SETUP.md | Database installation for all OS |
| DEPLOYMENT.md | Comprehensive deployment guide |
| API_TESTING.md | API testing with cURL, Postman, HTTPie |
| CONTRIBUTING.md | Development contribution guidelines |

### Source Code Structure

#### Controller Layer
- Handles HTTP requests
- Input validation
- Response formatting
- Example: `POST /api/auth/register`

#### Service Layer
- Core business logic
- Password hashing with BCrypt
- User validation
- Transaction management

#### Repository Layer
- Spring Data JPA
- Database queries
- Custom finder methods
- CRUD operations

#### Entity Layer
- JPA mappings
- Table definition (users)
- Unique constraints
- Timestamps

#### DTOs (Data Transfer Objects)
- Request/Response objects
- Input validation
- Separation of concerns

#### Exception Handling
- Custom exceptions
- Global exception handler
- HTTP status codes
- Error responses

### Configuration Files
```properties
application.properties      → Default (production baseline)
application-dev.properties  → Development (verbose logging)
application-prod.properties → Production (optimized)
application-staging.properties → Staging (balanced)
application-test.properties → Testing (H2 database)
```

### Database Migration
- **Filename Pattern:** V{version}__{description}.sql
- Uses Flyway for version control
- Automatic execution on startup
- Creates users table with indexes

### Test Files

#### AuthControllerTest.java
- 6 test cases
- Tests REST endpoints
- Mocks AuthService
- Validates responses

#### AuthServiceTest.java
- 7 test cases
- Tests business logic
- Mocks repository
- Validates error handling

### CI/CD

#### GitHub Actions (.github/workflows/ci-cd.yml)
- Runs on: push to main/develop, PRs
- Jobs: Build, Deploy Dev, Deploy Prod
- Uses: Java 17, Maven, SonarCloud

#### Jenkins (Jenkinsfile)
- Agent: Any
- Stages: Build → Test → Package → Deploy
- Parallel execution supported
- Docker integration

---

## 🔄 Data Flow

```
HTTP Request
    ↓
┌─────────────────────────────┐
│  AuthController             │  ← REST endpoints
│  POST /api/auth/register    │
│  POST /api/auth/login       │
└──────────┬──────────────────┘
           ↓
┌─────────────────────────────┐
│  AuthService                │  ← Business logic
│  - register()               │
│  - login()                  │
│  - Get user details        │
└──────────┬──────────────────┘
           ↓
┌─────────────────────────────┐
│  UserRepository             │  ← Data access
│  - findByEmail()            │
│  - existsByEmail()          │
│  - save()                   │
└──────────┬──────────────────┘
           ↓
┌─────────────────────────────┐
│  PostgreSQL Database        │  ← Data storage
│  - users table              │
│  - Encrypted passwords      │
└─────────────────────────────┘
           ↓
HTTP Response
```

---

## 🚀 Quick Reference

### Build Commands
```bash
mvn clean build              # Compile and test
mvn clean package            # Create JAR
mvn clean install            # Install to local Maven repo
mvn spring-boot:run          # Run application
```

### Test Commands
```bash
mvn test                     # Run all tests
mvn test -Dtest=ClassName   # Run specific test
mvn jacoco:report            # Coverage report
```

### Docker Commands
```bash
docker build -t auth-module .     # Build image
docker-compose up -d              # Start services
docker-compose logs -f            # View logs
docker-compose down               # Stop services
```

---

## 📊 Technology Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.1.5 |
| Language | Java 17 |
| Database | PostgreSQL 15 |
| ORM | Spring Data JPA |
| Security | Spring Security, BCrypt |
| Testing | JUnit 5, Mockito |
| Build | Maven 3.8+ |
| Containerization | Docker, Docker Compose |
| CI/CD | Jenkins, GitHub Actions |
| Schema Migration | Flyway |

---

## 🔐 Security Features

- ✅ BCrypt password hashing
- ✅ Input validation (Email, Name, Password)
- ✅ Unique email constraint
- ✅ Spring Security integration
- ✅ Error handling without stack traces
- ✅ Transaction management
- ✅ Production-ready configurations

---

## 📈 Future Enhancements

### Phase 1 (Near-term)
- JWT token generation
- Refresh tokens
- API rate limiting
- Email verification

### Phase 2 (Medium-term)
- OAuth2/OpenID Connect
- Multi-factor authentication
- Audit logging
- Role-based authorization

### Phase 3 (Long-term)
- Microservices split
- Kubernetes manifests
- Advanced monitoring
- Distributed tracing

---

## 🧪 Test Coverage

```
AuthControllerTest
  ✅ testRegisterSuccess
  ✅ testRegisterDuplicateUser
  ✅ testLoginSuccess
  ✅ testLoginInvalidCredentials
  ✅ testRegisterInvalidEmail
  ✅ testRegisterMissingFields
  ✅ testHealthCheck

AuthServiceTest
  ✅ testRegisterSuccess
  ✅ testRegisterDuplicateUser
  ✅ testLoginSuccess
  ✅ testLoginInvalidPassword
  ✅ testLoginUserNotFound
  ✅ testGetUserById
  ✅ testGetUserByEmail

Total: 13 test cases
Target Coverage: 80%+
```

---

## 🔗 File Dependencies

```
AuthModuleApplication.java
  └── AuthController.java
       └── AuthService.java
            ├── UserRepository.java
            │    └── User.java
            ├── PasswordEncoder (SecurityConfig.java)
            ├── RegisterRequest.java
            ├── LoginRequest.java
            └── AuthResponse.java
```

---

## 📋 Deployment Checklist

- [ ] All tests passing (13/13)
- [ ] Code coverage ≥ 80%
- [ ] Code review completed
- [ ] Documentation updated
- [ ] Database backups created
- [ ] Environment variables configured
- [ ] Docker image built and tested
- [ ] CI/CD pipeline configured
- [ ] Security scan completed
- [ ] Performance testing done

---

## 🆘 Getting Help

1. **Check Documentation**
   - README.md - Overview
   - API_TESTING.md - API examples
   - DEPLOYMENT.md - Deployment guide

2. **View Logs**
   ```bash
   docker logs auth-service
   docker-compose logs -f
   mvn clean test -X
   ```

3. **Common Issues**
   - See DEPLOYMENT.md troubleshooting section
   - Check DATABASE_SETUP.md for database issues

4. **Development**
   - See CONTRIBUTING.md for guidelines
   - Review existing code for patterns

---

**Last Updated:** April 2026  
**Project Status:** ✅ Production Ready  
**License:** MIT
