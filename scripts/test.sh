#!/usr/bin/env bash
set -euo pipefail

APP1_WF_DIR="$(pwd)/wf1"
APP2_WF_DIR="$(pwd)/wf2"

WF1_PORT_HTTPS=34566
WF2_PORT_HTTPS=23442

docker compose up -d

echo "Waiting for PostgreSQL to be ready..."

sleep 5

LOG_DIR="$(pwd)/logs"
mkdir -p "$LOG_DIR"

start_wildfly() {
  local wf_dir="$1"
  local name="$2"
  echo "Starting WildFly $name in $wf_dir ..."
  nohup "$wf_dir"/bin/standalone.sh -c standalone.xml > "$LOG_DIR/$name.log" 2>&1 &
  echo "$name started, log -> $LOG_DIR/$name.log"
}

start_wildfly "$APP1_WF_DIR" "app1"
start_wildfly "$APP2_WF_DIR" "app2"

echo "All WildFly instances started."

stop_wildfly() {
  local wf_dir="$1"
  local management_port="$2"
  echo "Stopping WildFly in $wf_dir on port $management_port ..."
  "$wf_dir/bin/jboss-cli.sh" --connect controller=localhost:"$management_port" command=:shutdown || true
}


echo "Press [ENTER] to stop WildFly instances..."
read -r

stop_wildfly "$APP1_WF_DIR" $((WF1_PORT_HTTPS+1))
stop_wildfly "$APP2_WF_DIR" $((WF2_PORT_HTTPS+1))
echo "All WildFly instances stopped."

docker compose down