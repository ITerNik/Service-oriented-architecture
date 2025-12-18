package ru.ifmo.calculatingservice.service;

import jakarta.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ifmo.calculatingservice.ejb.RouteCalculatorRemote;

@Service
public class RouteService {

    @Value("${collection-managing-service.url}")
    private String service1Url;

    private RouteCalculatorRemote routeCalculator;

    @PostConstruct
    public void init() {
        try {
            InitialContext ctx = new InitialContext();
            routeCalculator = (RouteCalculatorRemote)
                    ctx.lookup(
                            "java:global/calculating-service/calculating-service-ejb/RouteCalculatorBean!ru.ifmo.calculatingservice.ejb.RouteCalculatorRemote");
        } catch (NamingException e) {
            throw new RuntimeException("Failed to lookup EJB", e);
        }
    }

    public double calculateToMaxPopulated() {
        return routeCalculator.calculateToMaxPopulated(service1Url);
    }

    public double calculateBetweenOldestAndNewest() {
        return routeCalculator.calculateBetweenOldestAndNewest(service1Url);
    }
}
