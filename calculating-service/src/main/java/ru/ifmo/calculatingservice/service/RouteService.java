package ru.ifmo.calculatingservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ifmo.calculatingservice.model.City;

import java.util.Arrays;
import org.springframework.web.client.RestTemplate;
import java.util.Comparator;
import java.util.List;

@Service
public class RouteService {

    @Value("${collection-managing-service.url}")
    private String service1Url;

    private final RestTemplate restTemplate;

    public RouteService() {
        this.restTemplate = createRestTemplate();
    }

    private RestTemplate createRestTemplate() {
        return new RestTemplate();
    }

    public double calculateToMaxPopulated() {
        String url = service1Url + "/cities?size=1000";
        City[] cities = restTemplate.getForObject(url, City[].class);

        if (cities == null || cities.length == 0) {
            return 0.0;
        }

        City maxPopulated = Arrays.stream(cities)
                .max(Comparator.comparing(City::getPopulation))
                .orElseThrow();

        return calculateDistance(0, 0,
                maxPopulated.getCoordinates().getX(),
                maxPopulated.getCoordinates().getY());
    }

    public double calculateBetweenOldestAndNewest() {
        String url = service1Url + "/cities?size=1000";
        City[] cities = restTemplate.getForObject(url, City[].class);

        if (cities == null || cities.length < 2) {
            return 0.0;
        }

        List<City> cityList = Arrays.asList(cities);

        City oldest = cityList.stream()
                .min(Comparator.comparing(City::getCreationDate))
                .orElseThrow();

        City newest = cityList.stream()
                .max(Comparator.comparing(City::getCreationDate))
                .orElseThrow();

        return calculateDistance(
                oldest.getCoordinates().getX(),
                oldest.getCoordinates().getY(),
                newest.getCoordinates().getX(),
                newest.getCoordinates().getY());
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}