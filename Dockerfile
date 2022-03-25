FROM arm64v8/eclipse-temurin:17 AS build
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradlew build --no-daemon

FROM arm64v8/eclipse-temurin:17
RUN mkdir /app
COPY --from=build /home/gradle/output/*.jar /app/Mist.jar

ENTRYPOINT ["java", "-jar", "/app/Mist.jar"]