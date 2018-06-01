FROM openjdk:8-jdk-alpine
USER root

COPY /build/libs/greeting-rest-service-0.1.0.jar /app.jar

ENTRYPOINT [ "sh", "-c", "java -jar app.jar"]