FROM gradle:jdk17 as gradleimage
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle build

FROM eclipse-temurin:17.0.2_8-jdk-focal
COPY --from=gradleimage "/home/gradle/source/build/libs/bot-english-master-1.0.0.jar" /app/
WORKDIR /app
ENTRYPOINT ["java", "-jar", "bot-english-master-1.0.0.jar"]