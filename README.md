# Service-oriented-architecture

## Deployment

### Setup

```bash
git clone https://github.com/ITerNik/Service-oriented-architecture.git
bash scripts/install.sh
bash scripts/certs.sh
bash scripts/test.sh
```

## Description

### install.sh

Downloads wildfly, builds and deploys WAR files to three instances. Configures ports and backend URLs.

### certs.sh

Generates self-signed SSL certificates for WildFly instances and configures them. Imports certs to Java truststores. Also exports certificates for curl testing. (e.g. all-clients-combined.pem for Insomnia)

### test.sh

Starts PostgreSQL and three WildFly instances. Stops them after testing.

## Use

```bash
ssh -L 8080:localhost:8080 server918r818
```
