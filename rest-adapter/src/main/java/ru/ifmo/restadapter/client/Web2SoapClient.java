package ru.ifmo.restadapter.client;

import jakarta.xml.ws.Service;
import java.net.URI;
import java.net.URL;
import javax.xml.namespace.QName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Web2SoapClient {

    private final String web2SoapUrl;

    public Web2SoapClient(@Value("${web2.soap.url}") String web2SoapUrl) {
        this.web2SoapUrl = web2SoapUrl;
    }

    public double calculateToMaxPopulated() {
        try {
            RouteService service = getRouteService();
            return service.calculateToMaxPopulated();
        } catch (Exception e) {
            throw new RuntimeException("Failed to call web2 SOAP service", e);
        }
    }

    public double calculateBetweenOldestAndNewest() {
        try {
            RouteService service = getRouteService();
            return service.calculateBetweenOldestAndNewest();
        } catch (Exception e) {
            throw new RuntimeException("Failed to call web2 SOAP service", e);
        }
    }

    private RouteService getRouteService() throws Exception {
        URL wsdlURL = URI.create(web2SoapUrl).toURL();
        QName serviceName = new QName("http://itmo.ru/web2/route", "RouteService");

        Service service = Service.create(wsdlURL, serviceName);
        QName portName = new QName("http://itmo.ru/web2/route", "RouteServicePort");

        return service.getPort(portName, RouteService.class);
    }

    @jakarta.jws.WebService(name = "RouteService", targetNamespace = "http://itmo.ru/web2/route")
    public interface RouteService {

        @jakarta.jws.WebMethod
        double calculateToMaxPopulated();

        @jakarta.jws.WebMethod
        double calculateBetweenOldestAndNewest();
    }
}
