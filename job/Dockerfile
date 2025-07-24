FROM eclipse-temurin:11

WORKDIR /app

COPY target/job-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9004

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"] 