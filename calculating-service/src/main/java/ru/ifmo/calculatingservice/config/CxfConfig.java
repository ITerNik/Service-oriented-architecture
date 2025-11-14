package ru.ifmo.calculatingservice.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ifmo.calculatingservice.soap.RouteWebServiceImpl;

import jakarta.xml.ws.Endpoint;

@Configuration
public class CxfConfig {

    @Autowired
    private Bus bus;

    @Autowired
    private RouteWebServiceImpl routeWebServiceImpl;

    @Bean
    public Endpoint routeEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, routeWebServiceImpl);
        endpoint.publish("/RouteService");
        return endpoint;
    }
}
