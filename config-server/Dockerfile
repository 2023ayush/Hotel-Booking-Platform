# Use official OpenJDK 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Set working directory inside container
WORKDIR /app

# Copy the Maven/Gradle built jar into container
# Make sure your jar is built as target/config-server.jar
COPY target/config-server-0.0.1-SNAPSHOT.jar app.jar


# Expose the port configured in application.yml
EXPOSE 8888

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
