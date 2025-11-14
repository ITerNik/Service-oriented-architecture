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

### SOAP Endpoints

WSDL - `http://localhost:8080/CityService/CityWebService?wsdl`

POST `http://localhost:8080/CityService/CityWebService`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:coll="http://collectionmanagingservice.ifmo.ru/">
   <soapenv:Header/>
   <soapenv:Body>
      <coll:createCity>
         <city>
            <name>Amsterdam</name>
            <coordinates>
               <x>4.9</x>
               <y>52.37</y>
            </coordinates>
            <area>219</area>
            <population>872680</population>
            <metersAboveSeaLevel>-2</metersAboveSeaLevel>
            <capital>true</capital>
            <agglomeration>2.4</agglomeration>
            <climate>HUMIDCONTINENTAL</climate>
            <governor>
               <height>180.0</height>
               <birthday>1970-01-01</birthday>
            </governor>
         </city>
      </coll:createCity>
   </soapenv:Body>
</soapenv:Envelope>
```