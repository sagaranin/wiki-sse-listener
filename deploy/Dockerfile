FROM openjdk:8-jdk-alpine

COPY /var/lib/jenkins/workspace/wiki-sse-listener/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]