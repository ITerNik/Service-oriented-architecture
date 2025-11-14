package ru.ifmo.front.client;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService(targetNamespace = "http://soap.calculatingservice.ifmo.ru/", name = "RouteWebService")
public interface RouteWebServiceClient {

    @WebMethod
    Double calculateToMaxPopulated();

    @WebMethod
    Double calculateBetweenOldestAndNewest();
}
