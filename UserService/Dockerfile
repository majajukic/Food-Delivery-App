FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} userservice.jar

ENTRYPOINT ["java", "-jar", "/userservice.jar"]

EXPOSE 8085