FROM openjdk:8-jdk-alpine

RUN pwd
COPY ../target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]