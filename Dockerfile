FROM openjdk:11-jdk as build
WORKDIR /workspace/app
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .
COPY src src
RUN ./gradlew build

FROM openjdk:11-jdk
COPY --from=build /workspace/app/build/libs/trade-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
