package ru.ifmo.collectionmanagingservice.service;

import jakarta.inject.Inject;
import ru.ifmo.collectionmanagingservice.model.City;
import ru.ifmo.collectionmanagingservice.repository.CityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class CityService {
    @Inject
    private CityRepository repository;

    public List<City> getCities(int page, int size, String sort,
                                MultivaluedMap<String, String> filters) {

        Map<String, String> filterMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                filterMap.put(entry.getKey(), entry.getValue().get(0));
            }
        }

        return repository.findWithFilters(page, size, sort, filterMap);
    }

    @Transactional
    public City createCity(City city) {
        validateCity(city);
        city.setId(null); // Ensure ID is null for new entities
        city.setCreationDate(LocalDate.now());
        return repository.save(city);
    }

    public City getCityById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public City updateCity(Long id, City city) {
        Optional<City> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return null;
        }

        validateCity(city);
        city.setId(id);
        city.setCreationDate(existing.get().getCreationDate());
        return repository.save(city);
    }

    @Transactional
    public boolean deleteCity(Long id) {
        return repository.deleteById(id);
    }

    @Transactional
    public boolean deleteCityByMeters(Integer meters) {
        Optional<City> toDelete = repository.findFirstByMetersLessThan(meters);

        if (toDelete.isPresent()) {
            repository.delete(toDelete.get());
            return true;
        }
        return false;
    }

    public City getCityWithMinName() {
        return repository.findCityWithMinName().orElse(null);
    }

    public City getCityWithMaxClimate() {
        return repository.findCityWithMaxClimate().orElse(null);
    }

    private void validateCity(City city) {
        if (city.getName() == null || city.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (city.getCoordinates() == null) {
            throw new IllegalArgumentException("Coordinates required");
        }
        if (city.getCoordinates().getY() != null && city.getCoordinates().getY() < -194) {
            throw new IllegalArgumentException("Y coordinate must be >= -194");
        }
        if (city.getArea() == null || city.getArea() < 1) {
            throw new IllegalArgumentException("Area must be >= 1");
        }
        if (city.getPopulation() == null || city.getPopulation() < 1) {
            throw new IllegalArgumentException("Population must be >= 1");
        }
        if (city.getClimate() == null) {
            throw new IllegalArgumentException("Climate required");
        }
        if (city.getGovernor() == null) {
            throw new IllegalArgumentException("Governor required");
        }
        if (city.getGovernor().getHeight() == null || city.getGovernor().getHeight() < 1) {
            throw new IllegalArgumentException("Governor height must be >= 1");
        }
    }
}