FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY ../build/libs/web1-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
