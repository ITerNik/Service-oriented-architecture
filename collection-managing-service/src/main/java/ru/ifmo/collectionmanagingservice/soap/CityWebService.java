package ru.ifmo.collectionmanagingservice.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import ru.ifmo.collectionmanagingservice.model.City;

import java.util.List;

@WebService(targetNamespace = "http://soap.collectionmanagingservice.ifmo.ru/")
public interface CityWebService {

    @WebMethod
    List<City> getCities(
            @WebParam(name = "page") int page,
            @WebParam(name = "size") int size,
            @WebParam(name = "sort") String sort,
            @WebParam(name = "filters") String filters);

    @WebMethod
    City getCityById(@WebParam(name = "id") String id);

    @WebMethod
    City createCity(@WebParam(name = "city") City city);

    @WebMethod
    City updateCity(
            @WebParam(name = "id") String id,
            @WebParam(name = "city") City city);

    @WebMethod
    boolean deleteCity(@WebParam(name = "id") String id);

    @WebMethod
    boolean deleteCityByMeters(@WebParam(name = "meters") Integer meters);

    @WebMethod
    City getCityWithMinName();

    @WebMethod
    City getCityWithMaxClimate();
}
