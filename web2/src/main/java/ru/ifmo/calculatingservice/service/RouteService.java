package ru.ifmo.calculatingservice.service;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import ru.ifmo.calculatingservice.ejb.RouteCalculatorRemote;

public class RouteService {

    private String serviceUrl;
    private RouteCalculatorRemote routeCalculator;

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

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
        return routeCalculator.calculateToMaxPopulated(serviceUrl);
    }

    public double calculateBetweenOldestAndNewest() {
        return routeCalculator.calculateBetweenOldestAndNewest(serviceUrl);
    }
}
