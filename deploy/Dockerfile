FROM openjdk:8-jdk-alpine

COPY $WORKDIR/target/wiki-sse-listener-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]