#!/bin/bash

# Exit if any command fails
set -e

# Configurable variables
SSH_KEY=${SSH_KEY:-/c/Users/phanakhone/.ssh/id_ed25519}
REMOTE_USER=uatappadmin
REMOTE_HOST=10.83.3.151
REMOTE_DIR=/home/uatappadmin

# app port 8080
# host port 
DOCKER_PORT=7011

# Read version from Maven
NAME=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)


# Log all variables
echo "SSH_KEY=$SSH_KEY"
echo "REMOTE_USER=$REMOTE_USER"
echo "REMOTE_HOST=$REMOTE_HOST"
echo "REMOTE_DIR=$REMOTE_DIR"
echo "NAME=$NAME"
echo "VERSION=$VERSION"




# Build the Spring Boot application
echo "Building the Spring Boot application with version $VERSION..."
mvn clean package -DskipTests

# Build the Docker image
echo "Building Docker image with version $VERSION..."
docker build --build-arg VERSION=$VERSION --build-arg NAME=$NAME -t $NAME:$VERSION -t $NAME:latest .

# Save the Docker image
DOCKER_FILE="./$NAME-$VERSION.docker"
docker save -o $DOCKER_FILE $NAME:$VERSION

# Transfer the Docker image to the remote server
echo "Transferring Docker image to $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR..."
# scp -i "$SSH_KEY" "$DOCKER_FILE" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR"
scp "$DOCKER_FILE" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR"

# Optional: Run the remote script to reload the container
# ssh -i "$SSH_KEY" "$REMOTE_USER@$REMOTE_HOST" "bash ./reload-$NAME.sh $VERSION"

# Stop and remove the existing Docker container on the remote server
echo "Stopping and removing existing Docker container on the remote server..."
# ssh -i "$SSH_KEY" "$REMOTE_USER@$REMOTE_HOST" "docker rm -f $NAME || true"
ssh "$REMOTE_USER@$REMOTE_HOST" "docker rm -f $NAME || true"
ssh "$REMOTE_USER@$REMOTE_HOST" "docker image rm $NAME:$VERSION"

# Load the Docker image on the remote server
echo "Loading Docker image on the remote server..."
ssh "$REMOTE_USER@$REMOTE_HOST" "docker load -i $REMOTE_DIR/$NAME-$VERSION.docker"

# Run the Docker container on the remote server
echo "Running Docker container on the remote server..."
ssh "$REMOTE_USER@$REMOTE_HOST" "docker run -d --name $NAME -p $DOCKER_PORT:8080 $NAME:$VERSION"

# Run docker ps
echo "Running docker ps on the remote server..."
ssh "$REMOTE_USER@$REMOTE_HOST" "docker ps"

# Log docker -f
echo "Running docker logs -f on the remote server..."
ssh "$REMOTE_USER@$REMOTE_HOST" "docker logs -f $NAME"

echo "Deployment completed successfully."