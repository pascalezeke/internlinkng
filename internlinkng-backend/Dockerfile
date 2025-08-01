FROM openjdk:17-jdk-slim

WORKDIR /app

# Set environment variables to disable Gradle daemon
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.parallel=false -Dorg.gradle.caching=false"
ENV GRADLE_USER_HOME=/tmp/.gradle

# Copy the Gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Copy the source code
COPY src src

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application with daemon disabled and better error handling
RUN ./gradlew build --no-daemon --stacktrace --info

# Expose the port
EXPOSE 8080

# Run the application with proper environment handling
CMD ["./gradlew", "run", "--no-daemon", "--args=--port=${PORT:-8080}"] 