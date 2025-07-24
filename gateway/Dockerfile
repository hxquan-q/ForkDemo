FROM eclipse-temurin:11

WORKDIR /app

COPY target/gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"] 