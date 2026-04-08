#!/bin/bash

# Create directory structure
mkdir -p jplag/languages
mkdir -p storage/{uploads,extracted,reports}
mkdir -p docker
mkdir -p jenkins
mkdir -p scripts
mkdir -p logs
mkdir -p backups

# Create Docker files
touch docker/Dockerfile
touch docker/docker-compose.yml
touch docker/mongo-init.js
touch docker/prometheus.yml
touch docker/.dockerignore

# Create Jenkins file
touch jenkins/Jenkinsfile

# Create script files
touch scripts/setup.sh
touch scripts/deploy.sh
touch scripts/backup.sh

# Create root files
touch .gitignore
touch README.md
touch pom.xml
touch LICENSE
touch .env.example

echo "All files created successfully!"
echo "Now copy the content from the answer above into each file."
