package ru.ifmo.web1.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ifmo.web1.dto.ErrorResponse;
import ru.ifmo.web1.model.City;
import ru.ifmo.web1.service.CityService;

@RestController
@RequestMapping("/cities")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<?> getCities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {

        Map<String, String> filters = new HashMap<>();
        Page<City> cities = cityService.getCities(page, size, sort, filters);
        return ResponseEntity.ok(cities);
    }

    @PostMapping
    public ResponseEntity<?> createCity(@Valid @RequestBody City city) {
        try {
            City created = cityService.createCity(city);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(new ErrorResponse(422, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCityById(@PathVariable Long id) {
        City city = cityService.getCityById(id);
        if (city == null) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "City not found"));
        }
        return ResponseEntity.ok(city);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCity(@PathVariable Long id, @Valid @RequestBody City city) {
        try {
            City updated = cityService.updateCity(id, city);
            if (updated == null) {
                return ResponseEntity.status(404).body(new ErrorResponse(404, "City not found"));
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(new ErrorResponse(422, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable Long id) {
        boolean deleted = cityService.deleteCity(id);
        if (!deleted) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "City not found"));
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/special/less-than-meters")
    public ResponseEntity<?> deleteCityByMeters(@RequestParam(name = "less-than-meters") Integer meters) {
        if (meters == null) {
            return ResponseEntity.status(400).body(new ErrorResponse(400, "Parameter required"));
        }

        boolean deleted = cityService.deleteCityByMeters(meters);
        if (!deleted) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No city found"));
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/special/min-name")
    public ResponseEntity<?> getCityWithMinName() {
        City city = cityService.getCityWithMinName();
        if (city == null) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No cities found"));
        }
        return ResponseEntity.ok(city);
    }

    @GetMapping("/special/max-climate")
    public ResponseEntity<?> getCityWithMaxClimate() {
        City city = cityService.getCityWithMaxClimate();
        if (city == null) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "No cities found"));
        }
        return ResponseEntity.ok(city);
    }
}
