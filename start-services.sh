#!/bin/bash

set -e

echo "Starting service deployment..."

# Ensure Docker is installed
if ! command -v docker &> /dev/null; then
  echo "ERROR: Docker is not installed."
  exit 1
fi

# Ensure Docker daemon is running
if ! docker info &> /dev/null; then
  echo "ERROR: Docker is not running."
  echo "Try: sudo systemctl start docker"
  exit 1
fi

# Optional: pull latest changes
if [ -d .git ]; then
  echo "Pulling latest changes..."
  git pull --ff-only || {
    echo "WARNING: Git pull failed. Continuing with local code."
  }
fi

# Check for .env file
if [ ! -f .env ]; then
  echo "ERROR: .env file not found!"
  echo "Please create a .env file before running this script."
  exit 1
fi

# Docker Compose automatically loads .env — no export needed
echo "Building and starting services..."
docker compose up -d --build --remove-orphans

echo "==================================================="
echo "Services deployed successfully!"
echo "---------------------------------------------------"
echo "View logs:     docker compose logs -f"
echo "Stop services: docker compose down"
echo "==================================================="
