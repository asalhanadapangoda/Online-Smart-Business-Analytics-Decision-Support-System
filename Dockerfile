# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the pre-built jar from local target directory
COPY target/SmartBusinessAnalyticsAndDecisionSupportSystem-1.0-SNAPSHOT.jar app.jar

# Create reports directory
RUN mkdir ./reports

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
