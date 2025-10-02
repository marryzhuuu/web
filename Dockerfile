# Сервер приложения
FROM gradle:8.10-jdk17 AS build
COPY --chown=gradle:gradle .. /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle jar --no-daemon

FROM eclipse-temurin:17-jre
EXPOSE 7777
RUN mkdir /fcgi-bin
WORKDIR /fcgi-bin
COPY --from=build /home/gradle/src/build/libs/*.jar /fcgi-bin/web.jar
ENTRYPOINT ["java", "-DFCGI_PORT=7777", "-jar", "web.jar"]