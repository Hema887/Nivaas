# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the application's jar file into the container
COPY /nivaas/customer/target/customer.jar app.jar

# Expose the port your application runs on
EXPOSE 9092

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
