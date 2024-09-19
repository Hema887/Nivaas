# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the application's jar file into the container
COPY /api-gateway/target/api-gateway.jar app.jar

# Copy the keystore into the container (assuming it's already generated and exists in the resources folder)
COPY api-gateway/src/main/resources/keystore-localhost.jks /etc/ssl/certs/keystore-localhost.jks

# Expose the port your application runs on
EXPOSE 9443

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
