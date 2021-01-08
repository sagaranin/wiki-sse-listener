FROM openjdk:8-jdk-alpine

COPY /var/lib/jenkins/workspace/wiki-sse-listener/target/wiki-sse-listener-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]