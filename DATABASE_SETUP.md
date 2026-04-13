# Spring Boot Auth Module - Database Setup Guide

## PostgreSQL Setup (One-Time)

### macOS (using Homebrew)

```bash
# Install PostgreSQL
brew install postgresql@15

# Start PostgreSQL service
brew services start postgresql@15

# Connect to PostgreSQL
psql postgres

# Create database and user
CREATE DATABASE auth_db;
CREATE USER auth_user WITH PASSWORD 'auth_password';
ALTER ROLE auth_user SET client_encoding TO 'utf8';
ALTER ROLE auth_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE auth_user SET default_transaction_deferrable TO on;
ALTER ROLE auth_user SET timezone TO 'UTC';
GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;
ALTER DATABASE auth_db OWNER TO auth_user;

# Exit
\q
```

### Windows (using PostgreSQL Installer)

1. Download installer from: https://www.postgresql.org/download/windows/
2. Run the installer
3. Choose PostgreSQL 15+ version
4. Set password for postgres user
5. Port: 5432
6. Locale: [Default locale]
7. Complete installation

Then open Command Prompt as Administrator:

```cmd
# Connect to PostgreSQL
psql -U postgres

# Create database and user
CREATE DATABASE auth_db;
CREATE USER auth_user WITH PASSWORD 'auth_password';
ALTER ROLE auth_user SET client_encoding TO 'utf8';
GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;
ALTER DATABASE auth_db OWNER TO auth_user;

# Exit
\q
```

### Linux (Ubuntu/Debian)

```bash
# Update package manager
sudo apt-get update

# Install PostgreSQL
sudo apt-get install postgresql postgresql-contrib

# Access PostgreSQL
sudo -u postgres psql

# Create database and user
CREATE DATABASE auth_db;
CREATE USER auth_user WITH PASSWORD 'auth_password';
ALTER ROLE auth_user SET client_encoding TO 'utf8';
ALTER ROLE auth_user SET default_transaction_isolation TO 'read committed';
GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;
ALTER DATABASE auth_db OWNER TO auth_db;

# Exit
\q

# Start PostgreSQL service
sudo service postgresql start
```

---

## Verify Installation

```bash
# Connect with specific user
psql -U auth_user -d auth_db -h localhost

# List databases
\l

# List roles/users
\du

# Exit
\q
```

---

## Docker PostgreSQL (Alternative)

No installation needed - use Docker:

```bash
# Start PostgreSQL container
docker run --name auth-db \
  -e POSTGRES_DB=auth_db \
  -e POSTGRES_USER=auth_user \
  -e POSTGRES_PASSWORD=auth_password \
  -p 5432:5432 \
  -d postgres:15-alpine

# Connect
docker exec -it auth-db psql -U auth_user -d auth_db
```

---

## Update application.properties

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
spring.datasource.username=auth_user
spring.datasource.password=auth_password
```

---

Now run the application - Flyway will automatically create the schema!

```bash
mvn spring-boot:run
```
