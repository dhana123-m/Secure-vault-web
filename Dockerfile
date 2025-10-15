# Stage 1: Build
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace

COPY pom.xml . 
COPY src ./src

# Install Maven and build
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /workspace/target/secure-vault-web-1.0.0.jar /app/app.jar

# Optional data directory
RUN mkdir -p /data && chown -R 1000:1000 /data
VOLUME /data

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
