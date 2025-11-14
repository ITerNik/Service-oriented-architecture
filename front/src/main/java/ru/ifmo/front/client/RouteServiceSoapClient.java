package ru.ifmo.front.client;

import jakarta.xml.ws.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.net.URL;

@Component
public class RouteServiceSoapClient {

    @Value("${calculating-service.soap.url}")
    private String serviceUrl;

    private RouteWebServiceClient getPort() {
        try {
            URL wsdlURL = new URL(serviceUrl + "?wsdl");
            QName SERVICE_NAME = new QName("http://soap.calculatingservice.ifmo.ru/", "RouteService");
            Service service = Service.create(wsdlURL, SERVICE_NAME);
            return service.getPort(RouteWebServiceClient.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SOAP client", e);
        }
    }

    public Double calculateToMaxPopulated() {
        return getPort().calculateToMaxPopulated();
    }

    public Double calculateBetweenOldestAndNewest() {
        return getPort().calculateBetweenOldestAndNewest();
    }
}
