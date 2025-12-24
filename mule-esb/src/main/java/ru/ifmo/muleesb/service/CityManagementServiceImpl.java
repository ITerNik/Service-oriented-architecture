package ru.ifmo.muleesb.service;

import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ifmo.muleesb.client.Web1RestClient;
import ru.ifmo.muleesb.model.City;
import ru.ifmo.muleesb.model.PageResponse;

@Service
@WebService(
        serviceName = "CityManagementService",
        portName = "CityManagementPort",
        targetNamespace = "http://ifmo.ru/muleesb/cities",
        endpointInterface = "ru.ifmo.muleesb.service.CityManagementService")
public class CityManagementServiceImpl implements CityManagementService {

    private final Web1RestClient web1RestClient;

    @Autowired
    public CityManagementServiceImpl(Web1RestClient web1RestClient) {
        this.web1RestClient = web1RestClient;
    }

    @Override
    public PageResponse getCities(Integer page, Integer size, String sort) {
        return web1RestClient.getCities(page, size, sort);
    }

    @Override
    public City createCity(City city) {
        return web1RestClient.createCity(city);
    }

    @Override
    public City getCityById(Long id) {
        return web1RestClient.getCityById(id);
    }

    @Override
    public City updateCity(Long id, City city) {
        return web1RestClient.updateCity(id, city);
    }

    @Override
    public void deleteCity(Long id) {
        web1RestClient.deleteCity(id);
    }

    @Override
    public void deleteCityByMeters(Integer meters) {
        web1RestClient.deleteCityByMeters(meters);
    }

    @Override
    public City getCityWithMinimalName() {
        return web1RestClient.getCityWithMinimalName();
    }

    @Override
    public City getCityWithMaximalClimate() {
        return web1RestClient.getCityWithMaximalClimate();
    }
}
