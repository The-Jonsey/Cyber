#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim
VOLUME ["/home/app"]
WORKDIR /home/app
COPY src ./src
COPY pom.xml .
COPY mvnw .
EXPOSE 8080
RUN mvn dependency:resolve