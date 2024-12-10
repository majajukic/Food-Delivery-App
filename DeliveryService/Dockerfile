FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} deliveryservice.jar

ENTRYPOINT ["java", "-jar", "/deliveryservice.jar"]

EXPOSE 8084