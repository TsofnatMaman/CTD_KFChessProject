# Stage 1: Build the application
# Use a Maven image that includes the build tools and the Java SDK
FROM maven:3-openjdk-17 AS build
WORKDIR /app

# Copy the pom.xml files first to enable caching of dependencies.
# This makes subsequent builds faster if the dependencies haven't changed.
COPY pom.xml .
COPY server/pom.xml server/
COPY common/pom.xml common/
COPY client/pom.xml client/

# Copy the source code of the project
COPY common/src common/src
COPY server/src server/src
COPY client/src client/src

# Run the Maven package command. The -DskipTests flag skips the tests.
RUN mvn package -DskipTests

# Stage 2: Final (Runtime) Stage
# Use a smaller Java Runtime Environment image for the final application.
# This image is much smaller as it only contains what is needed to run the app.
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Expose the standard web server port.
# It is important that your Java code is configured to listen on this port.
EXPOSE 8080

# Copy the application's JAR file from the build stage into the final image.
# The --from=build flag specifies the source stage.
COPY --from=build /app/server/target/chess-websocket-server-1.0.0-shaded.jar app.jar

# Define the command to run the application when the container starts.
CMD ["java", "-jar", "app.jar"]