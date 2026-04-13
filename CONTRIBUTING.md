# Contributing to Auth Module

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Testing](#testing)
- [Commit Messages](#commit-messages)
- [Pull Requests](#pull-requests)

---

## Code of Conduct

Please be respectful and professional in all interactions.

---

## Getting Started

1. **Fork the repository**
   ```bash
   git clone https://github.com/your-username/auth-module.git
   cd auth-module
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Set up development environment**
   ```bash
   mvn clean install
   docker-compose up -d
   ```

---

## Development Workflow

### 1. Create Feature Branch

```bash
git checkout -b feature/add-jwt-support
# or for bug fixes
git checkout -b bugfix/fix-password-encoding
```

### 2. Make Changes

- Write clean, readable code
- Follow existing code style
- Add tests for new features
- Update documentation

### 3. Test Locally

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthControllerTest

# Run with code coverage
mvn jacoco:report
```

### 4. Commit Changes

```bash
git add .
git commit -m "Add JWT authentication support"
```

### 5. Push to Fork

```bash
git push origin feature/add-jwt-support
```

### 6. Create Pull Request

Open a PR against the `develop` branch with:
- Clear title
- Description of changes
- Related issue number (if any)
- Screenshots (if UI changes)

---

## Coding Standards

### Java Code Style

1. **Naming Conventions**
   - Classes: `UpperCamelCase` (e.g., `AuthController`)
   - Methods: `lowerCamelCase` (e.g., `getUserById`)
   - Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_LOGIN_ATTEMPTS`)

2. **Code Format**
   ```java
   // Good
   public class UserService {
       private final UserRepository repository;
       
       public UserService(UserRepository repository) {
           this.repository = repository;
       }
   }
   
   // Avoid
   public class UserService{
       private UserRepository repository;
       public UserService(UserRepository repository){
           this.repository=repository;
       }
   }
   ```

3. **Use Lombok**
   ```java
   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   public class User {
       private String email;
       private String name;
   }
   ```

4. **Error Handling**
   ```java
   try {
       // code
   } catch (SpecificException e) {
       log.error("Error message", e);
       throw new CustomException("User-friendly message");
   }
   ```

### File Organization

```
src/main/java/com/authmodule/
├── config/          # Configuration classes
├── controller/      # REST Controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA Entities
├── exception/       # Custom Exceptions
├── repository/      # Data Access Layer
└── service/         # Business Logic Layer
```

---

## Testing

### 1. Unit Tests

```java
@Test
@DisplayName("Should register user successfully")
void testRegisterSuccess() {
    // Arrange
    RegisterRequest request = new RegisterRequest(...);
    
    // Act
    AuthResponse response = authService.register(request);
    
    // Assert
    assertTrue(response.isSuccess());
}
```

### 2. Integration Tests

```java
@SpringBootTest
@ActiveProfiles("test")
class AuthControllerIntegrationTest {
    // Tests with real database
}
```

### 3. Test Coverage

- Minimum 80% code coverage
- All public methods tested
- Happy path and error scenarios

### 4. Running Tests

```bash
# All tests
mvn test

# With coverage report
mvn test jacoco:report

# Specific test
mvn test -Dtest=AuthServiceTest#testLoginSuccess
```

---

## Commit Messages

Follow conventional commits:

```
type(scope): subject

body

footer
```

### Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Build, dependencies, etc.

### Examples

```
feat(auth): add JWT token generation

Add JWT token generation for user login.
Include token expiration and refresh logic.

Closes #123
```

```
fix(repository): handle null email in findByEmail

Fix NPE when users table has null emails.
Add validation in repository method.
```

```
docs: update README with API examples

Add cURL examples for all API endpoints.
Include request/response samples.
```

---

## Pull Requests

### PR Title Format

```
[TYPE] Brief description

Example:
[FEATURE] Add JWT authentication support
[BUGFIX] Fix password encoding issue
[DOCS] Update API documentation
```

### PR Checklist

- [ ] Code follows style guidelines
- [ ] All tests pass
- [ ] Code coverage maintained (80%+)
- [ ] Documentation updated
- [ ] No breaking changes (or documented)
- [ ] Commits are squashed into logical chunks
- [ ] Rebased on latest develop branch

### PR Description Template

```markdown
## Description
Brief description of changes.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issues
Closes #123

## Testing Done
- Tested registration flow
- Verified password hashing
- Checked error scenarios

## Screenshots (if applicable)
[Add screenshots]

## Notes
Any additional context.
```

---

## Review Process

1. **Code Review**
   - At least 1 approval required
   - Automated tests must pass
   - No merge conflicts

2. **QA Testing**
   - Manual testing on staging
   - Integration testing
   - Performance testing

3. **Merge**
   - Squash commits
   - Merge to develop
   - Delete feature branch

---

## Setting Up IDE

### IntelliJ IDEA

1. Import project as Maven project
2. Enable annotation processors (Settings → Build → Compiler → Annotation Processors)
3. Set code style: Settings → Editor → Code Style → Java
4. Configure run configurations

### VS Code

1. Install extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Maven for Java

2. Configure settings in `.vscode/settings.json`

---

## Running Locally

```bash
# Full setup
mvn clean install
docker-compose up -d

# Run tests
mvn test

# Run application
mvn spring-boot:run

# Access at http://localhost:8080
```

---

## Common Issues

### Maven Build Fails

```bash
# Clear cache and rebuild
mvn clean install -U

# Skip tests
mvn clean install -DskipTests
```

### Docker Issues

```bash
# Stop all containers
docker-compose down -v

# Rebuild images
docker-compose up -d --build
```

### Database Connection Error

```bash
# Check PostgreSQL running
docker inspect postgres

# View logs
docker-compose logs postgres
```

---

## Questions?

- Check existing issues
- Ask in pull request comments
- Create a discussion topic

---

**Thank you for contributing! 🚀**
