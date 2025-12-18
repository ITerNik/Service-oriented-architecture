FROM quay.io/wildfly/wildfly:latest-jdk17

COPY front/build/libs/front.war /opt/jboss/wildfly/standalone/deployments/

COPY certs/app3/truststore.jks /opt/jboss/wildfly/standalone/configuration/truststore.jks

ENV JAVA_OPTS="-Djavax.net.ssl.trustStore=/opt/jboss/wildfly/standalone/configuration/truststore.jks -Djavax.net.ssl.trustStorePassword=changeit"

EXPOSE 8080
