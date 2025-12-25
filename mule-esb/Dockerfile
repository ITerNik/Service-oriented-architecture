FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY mule-esb/build/libs/mule-esb-1.0.0.jar app.jar
COPY certs/app1/truststore.jks /app/truststore.jks

EXPOSE 8081

ENTRYPOINT ["java", "-Djavax.net.ssl.trustStore=/app/truststore.jks", "-Djavax.net.ssl.trustStorePassword=changeit", "-jar", "app.jar"]
