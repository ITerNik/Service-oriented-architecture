package ru.ifmo.collectionmanagingservice.soap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jws.WebService;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import ru.ifmo.collectionmanagingservice.model.City;
import ru.ifmo.collectionmanagingservice.service.CityService;

import java.util.List;

@ApplicationScoped
@WebService(serviceName = "CityService", portName = "CityPort", targetNamespace = "http://soap.collectionmanagingservice.ifmo.ru/", endpointInterface = "ru.ifmo.collectionmanagingservice.soap.CityWebService")
public class CityWebServiceImpl implements CityWebService {

    @Inject
    private CityService cityService;

    @Override
    public List<City> getCities(int page, int size, String sort, String filters) {
        MultivaluedMap<String, String> filterMap = new MultivaluedHashMap<>();
        // Parse filters string if needed (can be empty for now)
        return cityService.getCities(page, size, sort, filterMap);
    }

    @Override
    public City getCityById(String id) {
        return cityService.getCityById(id);
    }

    @Override
    public City createCity(City city) {
        return cityService.createCity(city);
    }

    @Override
    public City updateCity(String id, City city) {
        return cityService.updateCity(id, city);
    }

    @Override
    public boolean deleteCity(String id) {
        return cityService.deleteCity(id);
    }

    @Override
    public boolean deleteCityByMeters(Integer meters) {
        return cityService.deleteCityByMeters(meters);
    }

    @Override
    public City getCityWithMinName() {
        return cityService.getCityWithMinName();
    }

    @Override
    public City getCityWithMaxClimate() {
        return cityService.getCityWithMaxClimate();
    }
}
