FROM openjdk:8-jdk-alpine

RUN git clone https://github.com/sagaranin/wiki-sse-listener.git
WORKDIR wiki-sse-listener

RUN mvn clean install
RUN cp target/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]