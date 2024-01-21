# Use an official OpenJDK runtime as a parent image
FROM openjdk:21

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the application JAR (replace 'your-application.jar' with the actual name)
COPY target/your-application.jar .

# Expose the port your application runs on (replace '8080' with the actual port)
EXPOSE 8080

# Specify the command to run on container start
CMD ["java", "-jar", "your-application.jar"]
