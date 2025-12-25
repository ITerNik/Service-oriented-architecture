package ru.ifmo.calculatingservice.ejb;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import ru.ifmo.calculatingservice.model.PageResponse;

@WebService(name = "CityManagementService", targetNamespace = "http://ifmo.ru/muleesb/cities")
public interface CityManagementService {

    @WebMethod(operationName = "getCities")
    @WebResult(name = "return", targetNamespace = "")
    PageResponse getCities(
            @WebParam(name = "page", targetNamespace = "") Integer page,
            @WebParam(name = "size", targetNamespace = "") Integer size,
            @WebParam(name = "sort", targetNamespace = "") String sort);
}
