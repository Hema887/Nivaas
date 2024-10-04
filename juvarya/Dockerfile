# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the application's jar file into the container
COPY /access-mgmt/target/access-mgmt.jar app.jar

# Expose the port your application runs on
EXPOSE 9097

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
