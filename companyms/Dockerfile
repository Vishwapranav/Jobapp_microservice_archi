# Use a base image with Java installed
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file into the container
# The assumption is that you've already run 'mvn package' to create the JAR
COPY target/*.jar app.jar

# Expose the port your application will run on
EXPOSE 8082

# Define the command to run your application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]