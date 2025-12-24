package ru.ifmo.muleesb.service;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import ru.ifmo.muleesb.model.City;
import ru.ifmo.muleesb.model.PageResponse;

@WebService(name = "CityManagementService", targetNamespace = "http://ifmo.ru/muleesb/cities")
public interface CityManagementService {

    @WebMethod
    PageResponse getCities(
            @WebParam(name = "page") Integer page,
            @WebParam(name = "size") Integer size,
            @WebParam(name = "sort") String sort);

    @WebMethod
    City createCity(@WebParam(name = "city") City city);

    @WebMethod
    City getCityById(@WebParam(name = "id") Long id);

    @WebMethod
    City updateCity(@WebParam(name = "id") Long id, @WebParam(name = "city") City city);

    @WebMethod
    void deleteCity(@WebParam(name = "id") Long id);

    @WebMethod
    void deleteCityByMeters(@WebParam(name = "meters") Integer meters);

    @WebMethod
    City getCityWithMinimalName();

    @WebMethod
    City getCityWithMaximalClimate();
}
