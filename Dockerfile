# Use a base image with OpenJDK 17
FROM openjdk:21-jdk-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Accept build arguments for version
ARG VERSION
ARG NAME
ENV NAME=$NAME

# Validate build arguments
RUN test -n "$VERSION" && test -n "$NAME"

# Copy the Maven build artifact (JAR file) from your local machine to the container
COPY target/${NAME}-${VERSION}.jar /app/${NAME}.jar

# Expose the application port (default Spring Boot port is 8080)
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["sh", "-c", "echo \"NAME is: $NAME\" && ls /app && java -jar /app/${NAME}.jar"]
