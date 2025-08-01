FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the Gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy the source code
COPY src src

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew build --no-daemon

# Expose the port
EXPOSE 8080

# Run the application with proper environment handling
CMD ["./gradlew", "run", "--args=--port=${PORT:-8080}"] 