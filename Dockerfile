# Build stage 1 : Build application
FROM maven:3.8.5 AS build
WORKDIR /home/app

COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Build stage 2 : Run application
FROM openjdk:17
WORKDIR /home/app/build

COPY --from=build /home/app/target/spring-file-upload-service-0.0.1.jar /home/app/build/app.jar
COPY --from=build /home/app/src/main/resources ./resources

EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dserver.port=8080","/home/app/build/app.jar"]
