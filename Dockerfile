FROM gradle:latest AS build

LABEL MAINTAINER="Ilya Dobryakov <aspirin@govno.tech>"

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:19-jre AS final
RUN mkdir /opt/app
WORKDIR /opt/app
COPY --from=build /home/gradle/src/build/libs/tankobon-*-all.jar /opt/app/tankobon.jar
ENTRYPOINT ["java","-jar","./tankobon.jar"]
