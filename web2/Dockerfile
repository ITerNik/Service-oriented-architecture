FROM quay.io/wildfly/wildfly:latest-jdk17

COPY web2/build/libs/calculating-service.war /opt/jboss/wildfly/standalone/deployments/
COPY certs/app2/truststore.jks /opt/jboss/wildfly/standalone/configuration/truststore.jks

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", \
     "-Djavax.net.ssl.trustStore=/opt/jboss/wildfly/standalone/configuration/truststore.jks", \
     "-Djavax.net.ssl.trustStorePassword=changeit"]
