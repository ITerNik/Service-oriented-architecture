package ru.ifmo.web1.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.web1.model.City;
import ru.ifmo.web1.repository.CityRepository;

@Service
public class CityService {
    private final CityRepository repository;

    public CityService(CityRepository repository) {
        this.repository = repository;
    }

    public Page<City> getCities(int page, int size, String sort, Map<String, String> filters) {
        // Parse sort parameter
        Sort sortOrder = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();
            String[] sortFields = sort.split(",");
            for (String field : sortFields) {
                boolean desc = field.startsWith("-");
                String fieldName = desc ? field.substring(1) : field;
                Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, fieldName));
            }
            sortOrder = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return repository.findAll(pageable);
    }

    @Transactional
    public City createCity(City city) {
        validateCity(city);
        city.setId(null); // Ensure ID is null for new entities
        if (city.getCreationDate() == null) {
            city.setCreationDate(LocalDate.now());
        }
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
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteCityByMeters(Integer meters) {
        Optional<City> toDelete = repository.findFirstByMetersAboveSeaLevelLessThan(meters);

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
