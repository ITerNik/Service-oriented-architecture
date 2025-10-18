#!/usr/bin/env bash
set -euo pipefail

WF_DIR="wildfly-31.0.1.Final"
WF_TAR="$WF_DIR.tar.gz"

WF1_DIR="wf1"
WF2_DIR="wf2"
WF3_DIR="wf3"

WF1_PORT_HTTP=31468
WF1_PORT_HTTPS=34566
WF2_PORT_HTTP=44968
WF2_PORT_HTTPS=23442
WF3_PORT_HTTP=8080
WF3_PORT_HTTPS=45312

WAR1_FILE="collection-managing-service/target/collection-managing-service-1.0-SNAPSHOT.war"
WAR2_FILE="calculating-service/target/calculating-service-0.0.1-SNAPSHOT.war"
WAR3_FILE="front/target/front-0.0.1-SNAPSHOT.war"

if [ -d "$WF_DIR" ]; then
	echo "$WF_DIR already exists — skipping download and extract."
else
	if [ -f "$WF_TAR" ]; then
		echo "$WF_TAR already exists — skipping download."
	else
		echo "Downloading $WF_TAR..."
		wget "https://github.com/wildfly/wildfly/releases/download/31.0.1.Final/$WF_TAR"
	fi

	echo "Extracting $WF_TAR..."
	tar -xzf "$WF_TAR"
fi

rm -rf wf1 wf2 wf3

cp -r "$WF_DIR"/ wf1/
cp -r "$WF_DIR"/ wf2/
cp -r "$WF_DIR"/ wf3/

echo "Switching ports in apps' configuration files"

set_service_port() {
  local file="$1"
  local property="$2"
  local port="$3"
  sed -i "s#^\($property=https://[^:]\+:\)[0-9]\+#\1$port#" "$file"
}

set_service_port "calculating-service/src/main/resources/application.properties" "collection-managing-service.url" "$WF1_PORT_HTTPS"
set_service_port "front/src/main/resources/application.properties" "backend.service1.url" "$WF1_PORT_HTTPS"
set_service_port "front/src/main/resources/application.properties" "backend.service2.url" "$WF2_PORT_HTTPS"

echo "mvn package"

mvn clean package

echo "Deploying WAR files to WildFly servers"

cp "$WAR1_FILE" "$WF1_DIR"/standalone/deployments/
echo "Deployed $WAR1_FILE to $WF1_DIR"

cp "$WAR2_FILE" "$WF2_DIR"/standalone/deployments/
echo "Deployed $WAR2_FILE to $WF2_DIR"

cp "$WAR3_FILE" "$WF3_DIR"/standalone/deployments/
echo "Deployed $WAR3_FILE to $WF3_DIR"


set_port() {
  local xml="$1"
  local listener="$2"
  local port="$3"
  sed -i "s#\(<socket-binding name=\"$listener\"[^>]*port=\"\)[^\"]*\"#\1$port\"#g" "$xml"
}

set_port "$WF1_DIR/standalone/configuration/standalone.xml" http "$WF1_PORT_HTTP"
set_port "$WF1_DIR/standalone/configuration/standalone.xml" https "$WF1_PORT_HTTPS"
set_port "$WF1_DIR/standalone/configuration/standalone.xml" management-http "$((WF1_PORT_HTTPS+1))"
set_port "$WF1_DIR/standalone/configuration/standalone.xml" management-https "$((WF1_PORT_HTTPS+2))"

set_port "$WF2_DIR/standalone/configuration/standalone.xml" http "$WF2_PORT_HTTP"
set_port "$WF2_DIR/standalone/configuration/standalone.xml" https "$WF2_PORT_HTTPS"
set_port "$WF2_DIR/standalone/configuration/standalone.xml" management-http "$((WF2_PORT_HTTPS+1))"
set_port "$WF2_DIR/standalone/configuration/standalone.xml" management-https "$((WF2_PORT_HTTPS+2))"

set_port "$WF3_DIR/standalone/configuration/standalone.xml" http "$WF3_PORT_HTTP"
set_port "$WF3_DIR/standalone/configuration/standalone.xml" https "$WF3_PORT_HTTPS"
set_port "$WF3_DIR/standalone/configuration/standalone.xml" management-http "$((WF3_PORT_HTTPS+1))"
set_port "$WF3_DIR/standalone/configuration/standalone.xml" management-https "$((WF3_PORT_HTTPS+2))"
