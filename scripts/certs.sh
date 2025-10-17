#!/usr/bin/env bash
set -euo pipefail

#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

APP1_NAME="app1"
APP2_NAME="app2"
APP3_NAME="app3"

APP1_WF_DIR="$(pwd)/wf1"
APP2_WF_DIR="$(pwd)/wf2"
APP3_WF_DIR="$(pwd)/wf3"

OUT_DIR="$(cd "$(dirname "$0")/.." && pwd)/certs"
mkdir -p "$OUT_DIR"

rm -rf "$OUT_DIR/$APP1_NAME" "$OUT_DIR/$APP2_NAME" "$OUT_DIR/$APP3_NAME"
mkdir -p "$OUT_DIR/$APP1_NAME" "$OUT_DIR/$APP2_NAME" "$OUT_DIR/$APP3_NAME"

PASS="changeit"

echo "Generating keystores and truststores for: $APP1_NAME, $APP2_NAME, $APP3_NAME"

gen_node() {
  local name="$1"
  local dir="$OUT_DIR/$name"
  echo "- Generating keystore for $name (CN=localhost, SAN=localhost,127.0.0.1)"
  keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -validity 3650 \
    -dname "CN=localhost,OU=soa2,O=Example,L=City,ST=State,C=US" \
    -ext SAN=DNS:localhost,IP:127.0.0.1 \
    -keystore "$dir/keystore.jks" -storepass "$PASS" -keypass "$PASS"
  keytool -exportcert -alias server -file "$dir/server.crt" -keystore "$dir/keystore.jks" -storepass "$PASS"
}

echo "Step 1: Generate server keystores and server certs"
gen_node "$APP1_NAME"
gen_node "$APP2_NAME"
gen_node "$APP3_NAME"

echo "Step 2: Create truststores and import other nodes' certs"
make_truststores() {
  local target="$1"
  local target_dir="$OUT_DIR/$target"
  keytool -importcert -noprompt -alias self -file "$target_dir/server.crt" -keystore "$target_dir/truststore.jks" -storepass "$PASS"
}

make_truststores "$APP1_NAME"
make_truststores "$APP2_NAME"
make_truststores "$APP3_NAME"

echo "Step 3: Import other certs into truststores"

import_cert_into_trust() {
  local src_name="$1"
  local dest_name="$2"
  local src_cert="$OUT_DIR/$src_name/server.crt"
  local dest_trust="$OUT_DIR/$dest_name/truststore.jks"
  keytool -importcert -noprompt -alias "$src_name" -file "$src_cert" -keystore "$dest_trust" -storepass "$PASS"
}

echo "Importing certs"

import_cert_into_trust "$APP1_NAME" "$APP2_NAME"
import_cert_into_trust "$APP1_NAME" "$APP3_NAME"
import_cert_into_trust "$APP2_NAME" "$APP1_NAME"
import_cert_into_trust "$APP2_NAME" "$APP3_NAME"
import_cert_into_trust "$APP3_NAME" "$APP1_NAME"
import_cert_into_trust "$APP3_NAME" "$APP2_NAME"

echo "Step 3: Deploy into WildFly servers"

ls -l "$OUT_DIR/$APP1_NAME/keystore.jks"
ls -ld "$APP1_WF_DIR/standalone/configuration/"
cp "$OUT_DIR/$APP1_NAME/keystore.jks" "$APP1_WF_DIR"/standalone/configuration/keystore.jks
echo "Copied keystore for $APP1_NAME"
cp "$OUT_DIR/$APP2_NAME/keystore.jks" "$APP2_WF_DIR"/standalone/configuration/keystore.jks
cp "$OUT_DIR/$APP3_NAME/keystore.jks" "$APP3_WF_DIR"/standalone/configuration/keystore.jks

cp "$OUT_DIR/$APP1_NAME/truststore.jks" "$APP1_WF_DIR"/standalone/configuration/truststore.jks
cp "$OUT_DIR/$APP2_NAME/truststore.jks" "$APP2_WF_DIR"/standalone/configuration/truststore.jks
cp "$OUT_DIR/$APP3_NAME/truststore.jks" "$APP3_WF_DIR"/standalone/configuration/truststore.jks

echo "Step 4: Insert system-properties into standalone.xml files"

insert_system_properties() {
  local wf_dir="$1"
  local xml="$wf_dir/standalone/configuration/standalone.xml"
  local keystore_path="\${jboss.server.config.dir}/keystore.jks"
  local truststore_path="\${jboss.server.config.dir}/truststore.jks"

  if grep -q "<system-properties>" "$xml"; then
    echo "system-properties already exist in $xml, skipping"
    return
  fi

  tmpfile=$(mktemp)
  cat << EOF > "$tmpfile"
<system-properties>
    <property name="javax.net.ssl.trustStore" value="$truststore_path"/>
    <property name="javax.net.ssl.trustStorePassword" value="$PASS"/>
    <property name="javax.net.ssl.keyStore" value="$keystore_path"/>
    <property name="javax.net.ssl.keyStorePassword" value="$PASS"/>
</system-properties>
EOF

  sed -i "/<server[^>]*>/r $tmpfile" "$xml"

  rm "$tmpfile"

  echo "Inserted system-properties into $xml"
}

insert_system_properties "$APP1_WF_DIR"
insert_system_properties "$APP2_WF_DIR"
insert_system_properties "$APP3_WF_DIR"

echo "Certs inserted and configured successfully."
