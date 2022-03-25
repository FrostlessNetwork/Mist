FROM arm64v8/eclipse-temurin:17 AS build
COPY --chown=nobody:nogroup . /home/nobody/src
WORKDIR /home/nobody/src
USER nobody
RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon

FROM arm64v8/eclipse-temurin:17
RUN mkdir /app
COPY --from=build /home/nobody/output/*.jar /app/Mist.jar

ENTRYPOINT ["java", "-jar", "/app/Mist.jar"]