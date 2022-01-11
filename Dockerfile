FROM gradle:latest AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:latest
COPY --from=build /home/gradle/src/build/libs/ /
ENTRYPOINT ["java","-jar","/tankobon-server-kotlin-standalone.jar"]
