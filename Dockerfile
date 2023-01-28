ARG BASE_IMAGE=eclipse-temurin:17.0.2_8-jdk-focal
FROM ${BASE_IMAGE} AS builder

RUN mkdir /source
WORKDIR /source

# Build the app
COPY . /source/
RUN ./gradlew --no-daemon build bootJar

FROM ${BASE_IMAGE}
COPY --from=builder "/source/build/libs/*.jar" /app.jar

CMD ["java", "-jar", "/app.jar"]

EXPOSE 8080