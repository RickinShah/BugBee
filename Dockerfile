FROM eclipse-temurin:21
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} bugbee.jar
#COPY target/BugBee-0.0.1-SNAPSHOT.jar bugbee.jar
ENTRYPOINT ["java", "-jar", "bugbee.jar"]