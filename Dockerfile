FROM fedora:41
WORKDIR /app
COPY target/oda-automation-service /app

CMD ["./oda-automation-service"]
