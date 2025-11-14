package ru.ifmo.collectionmanagingservice;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import ru.ifmo.collectionmanagingservice.dto.*;
import ru.ifmo.collectionmanagingservice.model.City;
import ru.ifmo.collectionmanagingservice.service.CityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@WebService(serviceName = "CityService", targetNamespace = "http://collectionmanagingservice.ifmo.ru/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class CityWebService {

    @EJB
    private CityService cityService;

    @WebMethod(operationName = "getCities")
    public GetCitiesResponse getCities(
            @WebParam(name = "page") Integer page,
            @WebParam(name = "size") Integer size,
            @WebParam(name = "sort") String sort,
            @WebParam(name = "filters") FilterMap filters) {

        try {
            int p = (page != null) ? page : 0;
            int s = (size != null) ? size : 10;
            Map<String, String> filterMap = (filters != null) ? filters.toMap() : new HashMap<>();

            List<City> cities = cityService.getCitiesWithFilters(p, s, sort, filterMap);

            GetCitiesResponse response = new GetCitiesResponse();
            response.setCities(cities);
            response.setSuccess(true);
            return response;
        } catch (Exception e) {
            GetCitiesResponse response = new GetCitiesResponse();
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @WebMethod(operationName = "createCity")
    public CreateCityResponse createCity(@WebParam(name = "city") City city) {
        CreateCityResponse response = new CreateCityResponse();
        try {
            City created = cityService.createCity(city);
            response.setCity(created);
            response.setSuccess(true);
            response.setStatusCode(201);
            return response;
        } catch (IllegalArgumentException e) {
            response.setSuccess(false);
            response.setStatusCode(422);
            response.setErrorMessage(e.getMessage());
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setStatusCode(500);
            response.setErrorMessage("Internal server error: " + e.getMessage());
            return response;
        }
    }

    @WebMethod(operationName = "getCityById")
    public GetCityResponse getCityById(@WebParam(name = "id") String id) {
        GetCityResponse response = new GetCityResponse();
        try {
            City city = cityService.getCityById(id);
            if (city == null) {
                response.setSuccess(false);
                response.setStatusCode(404);
                response.setErrorMessage("City not found");
            } else {
                response.setCity(city);
                response.setSuccess(true);
                response.setStatusCode(200);
            }
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setStatusCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @WebMethod(operationName = "updateCity")
    public UpdateCityResponse updateCity(
            @WebParam(name = "id") String id,
            @WebParam(name = "city") City city) {

        UpdateCityResponse response = new UpdateCityResponse();
        try {
            City updated = cityService.updateCity(id, city);
            if (updated == null) {
                response.setSuccess(false);
                response.setStatusCode(404);
                response.setErrorMessage("City not found");
            } else {
                response.setCity(updated);
                response.setSuccess(true);
                response.setStatusCode(200);
            }
            return response;
        } catch (IllegalArgumentException e) {
            response.setSuccess(false);
            response.setStatusCode(422);
            response.setErrorMessage(e.getMessage());
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setStatusCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @WebMethod(operationName = "deleteCity")
    public DeleteCityResponse deleteCity(@WebParam(name = "id") String id) {
        DeleteCityResponse response = new DeleteCityResponse();
        try {
            boolean deleted = cityService.deleteCity(id);
            if (!deleted) {
                response.setSuccess(false);
                response.setStatusCode(404);
                response.setErrorMessage("City not found");
            } else {
                response.setSuccess(true);
                response.setStatusCode(204);
            }
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setStatusCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @WebMethod(operationName = "deleteCityByMeters")
    public DeleteCityResponse deleteCityByMeters(@WebParam(name = "meters") Integer meters) {
        DeleteCityResponse response = new DeleteCityResponse();
        try {
            if (meters == null) {
                response.setSuccess(false);
                response.setStatusCode(400);
                response.setErrorMessage("Parameter 'meters' is required");
                return response;
            }

            boolean deleted = cityService.deleteCityByMeters(meters);
            if (!deleted) {
                response.setSuccess(false);
                response.setStatusCode(404);
                response.setErrorMessage("No city found");
            } else {
                response.setSuccess(true);
                response.setStatusCode(204);
            }
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setStatusCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @WebMethod(operationName = "getCityWithMinName")
    public GetCityResponse getCityWithMinName() {
        GetCityResponse response = new GetCityResponse();
        try {
            City city = cityService.getCityWithMinName();
            if (city == null) {
                response.setSuccess(false);
                response.setStatusCode(404);
                response.setErrorMessage("No cities found");
            } else {
                response.setCity(city);
                response.setSuccess(true);
                response.setStatusCode(200);
            }
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setStatusCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @WebMethod(operationName = "getCityWithMaxClimate")
    public GetCityResponse getCityWithMaxClimate() {
        GetCityResponse response = new GetCityResponse();
        try {
            City city = cityService.getCityWithMaxClimate();
            if (city == null) {
                response.setSuccess(false);
                response.setStatusCode(404);
                response.setErrorMessage("No cities found");
            } else {
                response.setCity(city);
                response.setSuccess(true);
                response.setStatusCode(200);
            }
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setStatusCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }
}