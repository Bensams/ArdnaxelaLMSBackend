# Stage 1: Build the JAR
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/library-management-system-*.jar /app/app.jar
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "app.jar"]