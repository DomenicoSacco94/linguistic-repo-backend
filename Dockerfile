FROM openjdk:17-jdk-alpine
VOLUME /main-app
ADD build/libs/backendRepo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar","/app.jar"]