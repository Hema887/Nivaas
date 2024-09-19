# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the application's jar file into the container
COPY /service-partners/target/service-partners.jar app.jar

# Expose the port your application runs on
EXPOSE 9098

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
