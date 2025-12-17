package ru.ifmo.calculatingservice.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.ifmo.calculatingservice.model.City;
import ru.ifmo.calculatingservice.model.PageResponse;

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
        ResponseEntity<PageResponse<City>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<PageResponse<City>>() {});

        List<City> cities = response.getBody() != null ? response.getBody().getContent() : null;

        if (cities == null || cities.isEmpty()) {
            return 0.0;
        }

        City maxPopulated =
                cities.stream().max(Comparator.comparing(City::getPopulation)).orElseThrow();

        return calculateDistance(
                0,
                0,
                maxPopulated.getCoordinates().getX(),
                maxPopulated.getCoordinates().getY());
    }

    public double calculateBetweenOldestAndNewest() {
        String url = service1Url + "/cities?size=1000";
        ResponseEntity<PageResponse<City>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<PageResponse<City>>() {});

        List<City> cities = response.getBody() != null ? response.getBody().getContent() : null;

        if (cities == null || cities.size() < 2) {
            return 0.0;
        }

        City oldest =
                cities.stream().min(Comparator.comparing(City::getCreationDate)).orElseThrow();

        City newest =
                cities.stream().max(Comparator.comparing(City::getCreationDate)).orElseThrow();

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
