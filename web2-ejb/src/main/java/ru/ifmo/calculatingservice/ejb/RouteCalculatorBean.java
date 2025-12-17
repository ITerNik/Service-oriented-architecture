package ru.ifmo.calculatingservice.ejb;

import jakarta.ejb.Stateless;
import java.util.Comparator;
import java.util.List;
import org.springframework.web.client.RestTemplate;
import ru.ifmo.calculatingservice.model.City;
import ru.ifmo.calculatingservice.model.PageResponse;

@Stateless
public class RouteCalculatorBean implements RouteCalculatorRemote {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public double calculateToMaxPopulated(String serviceUrl) {
        try {
            String fullUrl = serviceUrl + "/cities?page=1&size=100";
            PageResponse response = restTemplate.getForObject(fullUrl, PageResponse.class);
            if (response == null
                    || response.getContent() == null
                    || response.getContent().isEmpty()) {
                return 0.0;
            }

            List<City> cities = response.getContent();
            City maxPopulated = cities.stream()
                    .max(Comparator.comparing(City::getPopulation))
                    .orElse(null);

            if (maxPopulated == null) {
                return 0.0;
            }

            City startCity = cities.get(0);
            return calculateDistance(
                    startCity.getCoordinates().getX(),
                    startCity.getCoordinates().getY(),
                    maxPopulated.getCoordinates().getX(),
                    maxPopulated.getCoordinates().getY());
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate distance to max populated city", e);
        }
    }

    @Override
    public double calculateBetweenOldestAndNewest(String serviceUrl) {
        try {
            String fullUrl = serviceUrl + "/cities?page=1&size=100";
            PageResponse response = restTemplate.getForObject(fullUrl, PageResponse.class);
            if (response == null
                    || response.getContent() == null
                    || response.getContent().isEmpty()) {
                return 0.0;
            }

            List<City> cities = response.getContent();
            City oldest = cities.stream()
                    .min(Comparator.comparing(City::getCreationDate))
                    .orElse(null);

            City newest = cities.stream()
                    .max(Comparator.comparing(City::getCreationDate))
                    .orElse(null);

            if (oldest == null || newest == null) {
                return 0.0;
            }

            return calculateDistance(
                    oldest.getCoordinates().getX(),
                    oldest.getCoordinates().getY(),
                    newest.getCoordinates().getX(),
                    newest.getCoordinates().getY());
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate distance between oldest and newest cities", e);
        }
    }

    private double calculateDistance(Double x1, Double y1, Double x2, Double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
