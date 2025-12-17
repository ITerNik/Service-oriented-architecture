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
  echo "- Generating keystore for $name (CN=localhost, SAN=localhost,haproxy1,haproxy2,127.0.0.1)"
  keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -validity 3650 \
    -dname "CN=localhost,OU=soa2,O=Example,L=City,ST=State,C=US" \
    -ext SAN=DNS:localhost,DNS:haproxy1,DNS:haproxy2,IP:127.0.0.1 \
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

# Step 3: Deploy into WildFly servers (skipped if directories don't exist)
if [ -d "$APP1_WF_DIR/standalone/configuration/" ]; then
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

  echo "Step 4: Add SSL configuration to standalone.conf files"

  add_ssl_to_standalone_conf() {
    local wf_dir="$1"
    local conf="$wf_dir/bin/standalone.conf"

    if grep -q "javax.net.ssl.trustStore" "$conf"; then
      echo "SSL config already exists in $conf, skipping"
      return
    fi

    cat >> "$conf" << 'EOF'

# SSL/TLS Configuration for mutual TLS (added by certs.sh)
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=$JBOSS_HOME/standalone/configuration/truststore.jks"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStorePassword=changeit"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.keyStore=$JBOSS_HOME/standalone/configuration/keystore.jks"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.keyStorePassword=changeit"
EOF

    echo "Added SSL config to $conf"
  }

  add_ssl_to_standalone_conf "$APP1_WF_DIR"
  add_ssl_to_standalone_conf "$APP2_WF_DIR"
  add_ssl_to_standalone_conf "$APP3_WF_DIR"

  echo "Step 5: Configure WildFly Elytron to use our keystores"

  configure_wildfly_ssl() {
    local wf_dir="$1"
    local standalone_xml="$wf_dir/standalone/configuration/standalone.xml"
    
    echo "Configuring SSL in $standalone_xml"
    
    sed -i '/<tls>/,/<\/tls>/c\
            <tls>\
                <key-stores>\
                    <key-store name="serverKeyStore">\
                        <credential-reference clear-text="changeit"/>\
                        <implementation type="JKS"/>\
                        <file path="keystore.jks" relative-to="jboss.server.config.dir"/>\
                    </key-store>\
                    <key-store name="serverTrustStore">\
                        <credential-reference clear-text="changeit"/>\
                        <implementation type="JKS"/>\
                        <file path="truststore.jks" relative-to="jboss.server.config.dir"/>\
                    </key-store>\
                </key-stores>\
                <key-managers>\
                    <key-manager name="serverKeyManager" key-store="serverKeyStore">\
                        <credential-reference clear-text="changeit"/>\
                    </key-manager>\
                </key-managers>\
                <trust-managers>\
                    <trust-manager name="serverTrustManager" key-store="serverTrustStore"/>\
                </trust-managers>\
                <server-ssl-contexts>\
                    <server-ssl-context name="applicationSSC" key-manager="serverKeyManager" trust-manager="serverTrustManager"/>\
                </server-ssl-contexts>\
            </tls>' "$standalone_xml"
    
    echo "Configured SSL for $wf_dir"
  }

  configure_wildfly_ssl "$APP1_WF_DIR"
  configure_wildfly_ssl "$APP2_WF_DIR"
  configure_wildfly_ssl "$APP3_WF_DIR"
else
  echo "Step 3-5: Skipping WildFly deployment (directories not found)"
fi

echo "Certs and SSL configuration completed successfully."

# Step 6: Export PKCS#12 and PEM files for Postman/Insomnia
export_artifacts() {
  local name="$1"
  local dir="$OUT_DIR/$name"
  local p12="$dir/keystore.p12"
  local pem_cert="$dir/cert.pem"
  local pem_key="$dir/key.pem"

  echo "Exporting PKCS#12 and PEM for $name"

  # Export JKS -> PKCS12
  keytool -importkeystore -srckeystore "$dir/keystore.jks" -srcstorepass "$PASS" \
    -destkeystore "$p12" -deststoretype PKCS12 -deststorepass "$PASS" -srcalias server -destalias server -srckeypass "$PASS" -destkeypass "$PASS" -noprompt

  # Extract cert and key from PKCS12 using openssl
  openssl pkcs12 -in "$p12" -nodes -passin pass:"$PASS" -out "$dir/p12_all.pem"
  # Separate cert and key
  awk '/-----BEGIN CERTIFICATE-----/{flag=1} flag{print} /-----END CERTIFICATE-----/{flag=0}' "$dir/p12_all.pem" > "$pem_cert"
  awk '/-----BEGIN PRIVATE KEY-----/{flag=1} flag{print} /-----END PRIVATE KEY-----/{flag=0}' "$dir/p12_all.pem" > "$pem_key"
  rm -f "$dir/p12_all.pem"

  # Also provide a combined .pem (cert+key) which some tools accept
  cat "$pem_key" "$pem_cert" > "$dir/combined.pem"

  echo "Exported: $p12, $pem_cert, $pem_key, $dir/combined.pem"
}

export_artifacts "$APP1_NAME"
export_artifacts "$APP2_NAME"
export_artifacts "$APP3_NAME"

cat "$OUT_DIR/$APP1_NAME/combined.pem" "$OUT_DIR/$APP2_NAME/combined.pem" "$OUT_DIR/$APP3_NAME/combined.pem" > "$OUT_DIR/all-clients-combined.pem"
echo "Created all-clients bundle: $OUT_DIR/all-clients-combined.pem (all certs+keys)"