package ru.ifmo.calculatingservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ifmo.calculatingservice.client.CityServiceSoapClient;
import ru.ifmo.calculatingservice.model.City;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    @Autowired
    private CityServiceSoapClient cityServiceClient;

    public double calculateToMaxPopulated() {
        List<City> cities = cityServiceClient.getCities(0, 1000, null, null);

        if (cities == null || cities.isEmpty()) {
            return 0.0;
        }

        City maxPopulated = cities.stream()
                .max(Comparator.comparing(City::getPopulation))
                .orElseThrow();

        return calculateDistance(0, 0,
                maxPopulated.getCoordinates().getX(),
                maxPopulated.getCoordinates().getY());
    }

    public double calculateBetweenOldestAndNewest() {
        List<City> cities = cityServiceClient.getCities(0, 1000, null, null);

        if (cities == null || cities.size() < 2) {
            return 0.0;
        }

        List<City> valid = cities.stream()
                .filter(c -> c != null && c.getCreationDate() != null && c.getCoordinates() != null)
                .collect(Collectors.toList());

        if (valid.size() < 2) {
            return 0.0;
        }

        City oldest = valid.stream()
                .min(Comparator.comparing(City::getCreationDate))
                .orElseThrow();

        City newest = valid.stream()
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