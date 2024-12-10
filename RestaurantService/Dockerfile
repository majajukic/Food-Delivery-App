FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} restaurantservice.jar

ENTRYPOINT ["java", "-jar", "/restaurantservice.jar"]

EXPOSE 8081