FROM openjdk:8-jdk-alpine

RUN ls -l ${WORKSPACE}
COPY ${WORKSPACE}/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]