FROM fedora:41
LABEL org.opencontainers.image.source=https://github.com/opendonationasssistant/oda-automation-service
WORKDIR /app
COPY target/oda-automation-service /app

CMD ["./oda-automation-service"]
