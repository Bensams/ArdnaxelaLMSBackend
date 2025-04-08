# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the project JAR file into the container at /app
COPY target/library-management-system-0.0.1-SNAPSHOT.jar /app/ardnaxela-library-management-system.jar

# Make port 8443 available to the world outside this container
EXPOSE 8443

# Run the JAR file
ENTRYPOINT ["java", "-jar", "ardnaxela-library-management-system.jar"]