FROM arm64v8/eclipse-temurin:17 AS build
COPY --chown=grald:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17-alpine
RUN mkdir /app
COPY --from=build /home/gradle/output/*.jar /app/Mist.jar

ENTRYPOINT ["java", "-jar", "/app/Mist.jar"]