package ru.ifmo.muleesb.config;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ifmo.muleesb.service.CityManagementService;

@Configuration
public class WebServiceConfig {

    @Autowired
    private Bus bus;

    @Autowired
    private CityManagementService cityManagementService;

    @Bean
    public Endpoint cityManagementEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, cityManagementService);
        endpoint.publish("/cities");
        return endpoint;
    }
}
