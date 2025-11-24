# Use a multi-stage build to keep the image small
# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Give execution permission to mvnw
RUN chmod +x mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
# Note: We use ./mvnw dependency:go-offline to download dependencies
# If mvnw is missing, we might need to install maven or rely on the user having it.
# For now, assuming mvnw exists or will be added. 
# If not, we can use a maven image directly.
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
