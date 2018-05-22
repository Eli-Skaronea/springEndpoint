FROM openjdk:8-jdk-alpine

COPY /build/libs/gs-rest-service-0.1.0.jar /app.jar

ENTRYPOINT [ "sh", "-c", "java -jar app.jar"]