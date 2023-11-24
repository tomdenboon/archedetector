FROM maven:3.8.1-openjdk-16 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package spring-boot:repackage

FROM openjdk:16-slim

COPY --from=build /home/app/target/archedetector-0.0.1-SNAPSHOT.jar /usr/local/lib/app.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]