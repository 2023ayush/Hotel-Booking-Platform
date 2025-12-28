# Use official OpenJDK 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Set work directory inside container
WORKDIR /app

# Copy the Maven/Gradle built jar into container
# Make sure your jar is built as target/api-gateway.jar
COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar


# Expose the port configured in your application.yml
EXPOSE 5555

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
