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
        MultivaluedMap<String, String> filterMap = parseFilters(filters);
        return cityService.getCities(page, size, sort, filterMap);
    }

    MultivaluedHashMap<String, String> parseFilters(String filters) {
        MultivaluedMap<String, String> filterMap = new MultivaluedHashMap<>();

        if (filters != null && !filters.trim().isEmpty()) {
            String[] pairs = filters.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    if (!key.isEmpty() && !value.isEmpty()) {
                        filterMap.add(key, value);
                    }
                }
            }
        }

        return (MultivaluedHashMap<String, String>) filterMap;
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
