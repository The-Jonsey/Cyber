FROM openjdk:8-jre
MAINTAINER Louis Jones <louis@thejonsey.com>

ENTRYPOINT ["java", "-jar", "/usr/share/app/app.jar"]

ARG VER
ADD ./target/application-${VER}-exec.jar /usr/share/app/app.jar