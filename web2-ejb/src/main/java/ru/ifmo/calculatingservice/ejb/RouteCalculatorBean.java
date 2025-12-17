package ru.ifmo.calculatingservice.ejb;

import jakarta.ejb.Stateless;
import java.util.Comparator;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.ifmo.calculatingservice.model.City;
import ru.ifmo.calculatingservice.model.PageResponse;

@Stateless
public class RouteCalculatorBean implements RouteCalculatorRemote {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public double calculateToMaxPopulated(String serviceUrl) {
        try {
            String url = serviceUrl + "/cities?size=1000";
            ResponseEntity<PageResponse<City>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<PageResponse<City>>() {});

            List<City> cities = response.getBody() != null ? response.getBody().getContent() : null;

            if (cities == null || cities.isEmpty()) {
                return 0.0;
            }

            City maxPopulated = cities.stream()
                    .max(Comparator.comparing(City::getPopulation))
                    .orElseThrow();

            return calculateDistance(
                    0,
                    0,
                    maxPopulated.getCoordinates().getX(),
                    maxPopulated.getCoordinates().getY());
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate distance to max populated city", e);
        }
    }

    @Override
    public double calculateBetweenOldestAndNewest(String serviceUrl) {
        try {
            String url = serviceUrl + "/cities?size=1000";
            ResponseEntity<PageResponse<City>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<PageResponse<City>>() {});

            List<City> cities = response.getBody() != null ? response.getBody().getContent() : null;

            if (cities == null || cities.size() < 2) {
                return 0.0;
            }

            City oldest = cities.stream()
                    .min(Comparator.comparing(City::getCreationDate))
                    .orElseThrow();

            City newest = cities.stream()
                    .max(Comparator.comparing(City::getCreationDate))
                    .orElseThrow();

            return calculateDistance(
                    oldest.getCoordinates().getX(),
                    oldest.getCoordinates().getY(),
                    newest.getCoordinates().getX(),
                    newest.getCoordinates().getY());
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate distance between oldest and newest cities", e);
        }
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
