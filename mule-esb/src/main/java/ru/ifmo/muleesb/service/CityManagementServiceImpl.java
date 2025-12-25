package ru.ifmo.muleesb.service;

import jakarta.jws.WebService;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ifmo.muleesb.model.City;
import ru.ifmo.muleesb.model.PageResponse;

@Service
@WebService(
        serviceName = "CityManagementService",
        portName = "CityManagementPort",
        targetNamespace = "http://ifmo.ru/muleesb/cities",
        endpointInterface = "ru.ifmo.muleesb.service.CityManagementService")
public class CityManagementServiceImpl implements CityManagementService {

    private final ProducerTemplate producerTemplate;

    @Autowired
    public CityManagementServiceImpl(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @Override
    public PageResponse getCities(Integer page, Integer size, String sort) {
        System.out.println("getCities called with: page=" + page + ", size=" + size + ", sort=" + sort);
        PageResponse result =
                producerTemplate.requestBody("direct:getCities", new Object[] {page, size, sort}, PageResponse.class);
        System.out.println("getCities result: " + result);
        return result;
    }

    @Override
    public City createCity(City city) {
        return producerTemplate.requestBody("direct:createCity", city, City.class);
    }

    @Override
    public City getCityById(Long id) {
        return producerTemplate.requestBody("direct:getCityById", id, City.class);
    }

    @Override
    public City updateCity(Long id, City city) {
        return producerTemplate.requestBodyAndHeader("direct:updateCity", city, "id", id, City.class);
    }

    @Override
    public void deleteCity(Long id) {
        producerTemplate.requestBody("direct:deleteCity", id);
    }

    @Override
    public void deleteCityByMeters(Integer meters) {
        producerTemplate.requestBody("direct:deleteCityByMeters", meters);
    }

    @Override
    public City getCityWithMinimalName() {
        return producerTemplate.requestBody("direct:getCityWithMinimalName", null, City.class);
    }

    @Override
    public City getCityWithMaximalClimate() {
        return producerTemplate.requestBody("direct:getCityWithMaximalClimate", null, City.class);
    }
}
