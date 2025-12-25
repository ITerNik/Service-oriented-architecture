package ru.ifmo.calculatingservice.controller;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import ru.ifmo.calculatingservice.service.RouteService;

@WebService(serviceName = "RouteService", targetNamespace = "http://ifmo.ru/web2/route")
public class RouteController {

    @Resource
    private WebServiceContext wsContext;

    private RouteService getRouteService() {
        MessageContext mc = wsContext.getMessageContext();
        ServletContext sc = (ServletContext) mc.get(MessageContext.SERVLET_CONTEXT);
        return (RouteService) sc.getAttribute("routeService");
    }

    @WebMethod
    public double calculateToMaxPopulated() {
        RouteService routeService = getRouteService();
        if (routeService == null) {
            throw new RuntimeException("RouteService not initialized");
        }
        return routeService.calculateToMaxPopulated();
    }

    @WebMethod
    public double calculateBetweenOldestAndNewest() {
        RouteService routeService = getRouteService();
        if (routeService == null) {
            throw new RuntimeException("RouteService not initialized");
        }
        return routeService.calculateBetweenOldestAndNewest();
    }
}
