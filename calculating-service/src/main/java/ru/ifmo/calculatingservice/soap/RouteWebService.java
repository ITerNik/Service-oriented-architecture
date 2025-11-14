package ru.ifmo.calculatingservice.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService(targetNamespace = "http://soap.calculatingservice.ifmo.ru/")
public interface RouteWebService {

    @WebMethod
    Double calculateToMaxPopulated();

    @WebMethod
    Double calculateBetweenOldestAndNewest();
}
