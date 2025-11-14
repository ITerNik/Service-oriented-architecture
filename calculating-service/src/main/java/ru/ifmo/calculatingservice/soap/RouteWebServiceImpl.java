package ru.ifmo.calculatingservice.soap;

import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ifmo.calculatingservice.service.RouteService;

@Component
@WebService(serviceName = "RouteService", portName = "RoutePort", targetNamespace = "http://soap.calculatingservice.ifmo.ru/", endpointInterface = "ru.ifmo.calculatingservice.soap.RouteWebService")
public class RouteWebServiceImpl implements RouteWebService {

    @Autowired
    private RouteService routeService;

    @Override
    public Double calculateToMaxPopulated() {
        return routeService.calculateToMaxPopulated();
    }

    @Override
    public Double calculateBetweenOldestAndNewest() {
        return routeService.calculateBetweenOldestAndNewest();
    }
}
