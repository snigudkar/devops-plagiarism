## 🧪 API Testing Guide

### Using cURL

#### 1. Register New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "name": "John Doe",
    "password": "securePassword123"
  }'
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "user": {
    "id": 1,
    "email": "john@example.com",
    "name": "John Doe",
    "role": "USER"
  }
}
```

---

#### 2. Login User

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "securePassword123"
  }'
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "",
  "user": {
    "id": 1,
    "email": "john@example.com",
    "name": "John Doe",
    "role": "USER"
  }
}
```

---

#### 3. Health Check

```bash
curl http://localhost:8080/api/auth/health
```

**Expected Response:**
```
Auth Service is running
```

---

### Using Postman

#### 1. Import Collection

Create a new Postman collection with the following requests:

**Collection Name:** Auth Module API

---

#### 2. Register Request

```
POST http://localhost:8080/api/auth/register

Headers:
Content-Type: application/json

Body (JSON):
{
  "email": "test@example.com",
  "name": "Test User",
  "password": "password123"
}
```

---

#### 3. Login Request

```
POST http://localhost:8080/api/auth/login

Headers:
Content-Type: application/json

Body (JSON):
{
  "email": "test@example.com",
  "password": "password123"
}
```

---

#### 4. Health Check Request

```
GET http://localhost:8080/api/auth/health
```

---

### Using Postman Environment Variables

Create a Postman environment:

```json
{
  "name": "Auth Module",
  "values": [
    {
      "key": "base_url",
      "value": "http://localhost:8080"
    },
    {
      "key": "email",
      "value": "user@example.com"
    },
    {
      "key": "password",
      "value": "password123"
    }
  ]
}
```

Then use in requests:
- `{{base_url}}/api/auth/register`
- `{{email}}`
- `{{password}}`

---

### Using HTTPie (Alternative to cURL)

Install HTTPie: https://httpie.io/

```bash
# Register
http POST localhost:8080/api/auth/register \
  email=test@example.com \
  name="Test User" \
  password=password123

# Login
http POST localhost:8080/api/auth/login \
  email=test@example.com \
  password=password123

# Health check
http localhost:8080/api/auth/health
```

---

### Error Scenarios

#### Duplicate User Registration

```bash
# First registration (success)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "same@example.com", "name": "User", "password": "pass123"}'

# Second registration with same email (409 Conflict)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "same@example.com", "name": "User2", "password": "pass123"}'
```

**Response:**
```json
{
  "error": "User with email same@example.com already exists",
  "status": "409"
}
```

---

#### Invalid Password on Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "wrongPassword"}'
```

**Response (401 Unauthorized):**
```json
{
  "error": "Invalid email or password",
  "status": "401"
}
```

---

#### Invalid Email Format

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "invalid-email", "name": "User", "password": "pass123"}'
```

**Response (400 Bad Request):**
```json
{
  "email": "Email should be valid"
}
```

---

### Using IntelliJ IDEA REST Client

Create a file `api-test.http`:

```http
### Register User
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "intellij@example.com",
  "name": "IntelliJ User",
  "password": "password123"
}

###
### Login User
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "intellij@example.com",
  "password": "password123"
}

###
### Health Check
GET http://localhost:8080/api/auth/health
```

Then click "Run" in the editor!

---

### Performance Testing with Apache JMeter

1. Download JMeter from: https://jmeter.apache.org/download_jmeter.html
2. Create a Test Plan
3. Add Thread Group (number of users)
4. Add HTTP Request Sampler
5. Configure:
   - Server Name: localhost
   - Port: 8080
   - Path: /api/auth/register
   - Method: POST
   - Body data: `{"email":"test@example.com","name":"Test","password":"pass"}`

---

### Load Testing with Apache Bench

```bash
# Test registration endpoint - 100 requests, 10 concurrent
ab -n 100 -c 10 -p payload.json -T application/json \
  http://localhost:8080/api/auth/register

# Test login endpoint
ab -n 100 -c 10 -p login.json -T application/json \
  http://localhost:8080/api/auth/login

# Test health check endpoint
ab -n 1000 -c 20 http://localhost:8080/api/auth/health
```

---

## Tips & Tricks

1. **Save responses to file:**
   ```bash
   curl ... > response.json
   ```

2. **Pretty print JSON:**
   ```bash
   curl ... | json_pp
   # or
   curl ... | python3 -m json.tool
   ```

3. **Include response headers:**
   ```bash
   curl -i http://localhost:8080/api/auth/health
   ```

4. **Show only response headers:**
   ```bash
   curl -I http://localhost:8080/api/auth/health
   ```

5. **Debug with verbose output:**
   ```bash
   curl -v http://localhost:8080/api/auth/health
   ```

---

## Expected HTTP Status Codes

| Scenario | Status Code | Meaning |
|----------|-------------|---------|
| Successful registration | 201 | Created |
| Successful login | 200 | OK |
| User already exists | 409 | Conflict |
| Invalid credentials | 401 | Unauthorized |
| Invalid input | 400 | Bad Request |
| Server error | 500 | Internal Server Error |

---

**Happy Testing! 🎉**
