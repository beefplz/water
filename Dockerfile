FROM openjdk:17
CMD ["./gradlew", "clean", "build"]
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} nursery-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "nursery-0.0.1-SNAPSHOT.jar"]
