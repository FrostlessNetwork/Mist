FROM arm64v8/gradle:latest AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon

FROM arm64v8/eclipse-temurin:17
RUN mkdir /app
COPY --from=build /home/gradle/src/output/*.jar /app/Mist.jar
COPY --from=build /home/gradle/src/resources/log4j2.xml /app/log4j2.xml

ENTRYPOINT ["java", "-jar", "-Dlog4j.configurationFile=/app/log4j2.xml", "/app/Mist.jar"]