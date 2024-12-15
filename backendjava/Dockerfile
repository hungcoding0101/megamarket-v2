FROM eclipse-temurin:23

ARG JAR_FILE=target/*.jar

COPY ./target/megamarketv2-0.0.1-SNAPSHOT.jar app.jar


ENTRYPOINT [ "java", "-jar" , "/app.jar" ]
