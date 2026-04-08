# Plagiarism Detection System

An enterprise-grade plagiarism detection platform built with Spring Boot, MongoDB, and JPlag.

## Features

- 🔐 JWT-based authentication and authorization
- 📤 Asynchronous job processing for large submissions
- 📊 JPlag integration for code similarity detection
- 📈 Delta analysis with configurable thresholds
- 🗄️ MongoDB for flexible document storage
- 🐳 Docker containerization
- 📊 Prometheus and Grafana monitoring
- 🚀 CI/CD ready with Jenkins pipeline

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.1.x
- **Security**: Spring Security, JWT
- **Database**: MongoDB
- **Plagiarism Engine**: JPlag CLI integration
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, TestContainers
- **DevOps**: Docker, Docker Compose, Jenkins
- **Monitoring**: Prometheus, Grafana, Spring Actuator

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker and Docker Compose
- MongoDB 6.0+ (or use Docker)
- 4GB RAM minimum (8GB recommended)

## Quick Start

### Using Docker (Recommended)

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/plagiarism-detection-system.git
   cd plagiarism-detection-system