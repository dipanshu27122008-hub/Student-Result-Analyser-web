# ========================================================
# Multi-stage Dockerfile for Student Result Analysis System
# Compatible with Render, Railway, Fly.io, AWS, and GCP
# ========================================================

# Stage 1: Build application WAR with Maven & OpenJDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy project source code and sample data
COPY src ./src
COPY sample-data ./sample-data

# Build production WAR
RUN mvn clean package -DskipTests

# Stage 2: Production Runtime with Apache Tomcat 10.1 (Jakarta EE 10)
FROM tomcat:10.1-jdk21-temurin

# Remove default Tomcat web applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy built WAR as ROOT.war so it serves directly at root path "/"
COPY --from=builder /app/target/student-result-analysis.war /usr/local/tomcat/webapps/ROOT.war

# Copy sample data into container for reference
COPY sample-data /usr/local/tomcat/sample-data

# Default port exposed by Tomcat & cloud providers
EXPOSE 8080

# Start Tomcat in foreground
CMD ["catalina.sh", "run"]
