package ru.ifmo.calculatingservice.ejb;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import ru.ifmo.calculatingservice.model.PageResponse;

@WebService(name = "CityManagementService", targetNamespace = "http://ifmo.ru/muleesb/cities")
public interface CityManagementService {

    @WebMethod
    PageResponse getCities(
            @WebParam(name = "page") Integer page,
            @WebParam(name = "size") Integer size,
            @WebParam(name = "sortBy") String sortBy,
            @WebParam(name = "sortOrder") String sortOrder);
}
