FROM docker.io/library/eclipse-temurin:21-jdk-jammy
LABEL org.opencontainers.image.source=https://github.com/opendonationasssistant/oda-automation-service
WORKDIR /app
COPY target/oda-automation-service-0.1.jar /app

CMD ["java","--add-opens","java.base/java.time=ALL-UNNAMED","-jar","oda-automation-service-0.1.jar"]
