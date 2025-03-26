#!/bin/bash

# Exit if any command fails
set -e

# Read version from version.txt
NAME=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
VERSION=$(mvn help:evaluate -Dexpression=app.version -q -DforceStdout)

# Set the project version in Maven
echo "Building the Spring Boot application with version $VERSION..."
mvn clean package -DskipTests -Dproject.version=$VERSION

# Build the Docker image, passing the version as a build argument
echo "Building Docker image with version $VERSION..."
docker build --build-arg VERSION=$VERSION -t $NAME:$VERSION -t $NAME:latest .

docker save -o ./$NAME-$VERSION.docker $NAME:$VERSION

# @dminuat$24
scp -i /c/Users/phanakhone/.ssh/id_ed25519 ./$NAME-$VERSION.docker uatappadmin@10.83.3.151:/home/uatappadmin

# docker load -i ./my-allianz-api-backup-$version.docker
# docker run -d -it --restart always -p 8086:8080/tcp $NAME:$VERSION

# down docker container
ssh -i /c/Users/phanakhone/.ssh/id_ed25519 uatappadmin@10.83.3.151 "docker rm -f $NAME"

# run remote script
# ssh -i /c/Users/phanakhone/.ssh/id_ed25519 uatappadmin@10.83.3.151 "bash ./reload-$NAME.sh $VERSION"