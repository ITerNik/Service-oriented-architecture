package ru.ifmo.front.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.ifmo.front.client.CityServiceSoapClient;
import ru.ifmo.front.model.City;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/cities")
public class CitiesProxyController {

    private static final Logger logger = LoggerFactory.getLogger(CitiesProxyController.class);

    @Autowired
    private CityServiceSoapClient cityServiceSoapClient;

    @GetMapping
    public ResponseEntity<List<City>> getCities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam Map<String, String> allParams) {
        try {
            String filters = allParams.entrySet().stream()
                    .filter(e -> !e.getKey().equals("page") && !e.getKey().equals("size") && !e.getKey().equals("sort"))
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            if (filters.isEmpty()) {
                filters = null;
            }

            logger.debug("Getting cities: page={}, size={}, sort={}, filters={}", page, size, sort, filters);
            List<City> cities = cityServiceSoapClient.getCities(page, size, sort, filters);
            logger.debug("Retrieved {} cities", cities != null ? cities.size() : 0);
            if (cities != null && !cities.isEmpty()) {
                logger.debug("First city: {}", cities.get(0).getName());
            }
            return ResponseEntity.ok(cities != null ? cities : List.of());
        } catch (Exception e) {
            logger.error("Error getting cities", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable String id) {
        try {
            City city = cityServiceSoapClient.getCityById(id);
            return city != null ? ResponseEntity.ok(city) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting city by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody City city) {
        try {
            City created = cityServiceSoapClient.createCity(city);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            logger.error("Error creating city", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable String id, @RequestBody City city) {
        try {
            City updated = cityServiceSoapClient.updateCity(id, city);
            return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating city: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable String id) {
        try {
            boolean deleted = cityServiceSoapClient.deleteCity(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting city: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/meters/{meters}")
    public ResponseEntity<Void> deleteCityByMeters(@PathVariable Integer meters) {
        try {
            boolean deleted = cityServiceSoapClient.deleteCityByMeters(meters);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting city by meters: {}", meters, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/min-name")
    public ResponseEntity<City> getCityWithMinName() {
        try {
            City city = cityServiceSoapClient.getCityWithMinName();
            return city != null ? ResponseEntity.ok(city) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting city with min name", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/max-climate")
    public ResponseEntity<City> getCityWithMaxClimate() {
        try {
            City city = cityServiceSoapClient.getCityWithMaxClimate();
            return city != null ? ResponseEntity.ok(city) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting city with max climate", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
