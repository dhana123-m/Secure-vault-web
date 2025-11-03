# ===============================
# Stage 1: Build the application
# ===============================
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

# Install Maven and build the project
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests

# ===============================
# Stage 2: Runtime
# ===============================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the jar from build stage
COPY --from=build /workspace/target/*.jar /app/app.jar

# Create a data directory for CSV or vault files
RUN mkdir -p /data && chown -R 1000:1000 /data
VOLUME ["/data"]

# Expose the port Spring Boot runs on
EXPOSE 9092

# Run the Spring Boot app and store CSV in /data
ENTRYPOINT ["java","-jar","/app/app.jar","--vault.path=/data/vault.csv"]
