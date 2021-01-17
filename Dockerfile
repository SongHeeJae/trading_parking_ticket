FROM openjdk:11-jdk

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} parkingticket.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/parkingticket.jar"]
