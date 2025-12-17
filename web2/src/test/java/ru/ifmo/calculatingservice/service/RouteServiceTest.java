package ru.ifmo.calculatingservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import ru.ifmo.calculatingservice.model.City;
import ru.ifmo.calculatingservice.model.Coordinates;
import ru.ifmo.calculatingservice.model.PageResponse;

public class RouteServiceTest {

    private RouteService routeService;
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        routeService = new RouteService();
        restTemplate = Mockito.mock(RestTemplate.class);

        // Inject mocked RestTemplate and URL
        ReflectionTestUtils.setField(routeService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(routeService, "service1Url", "http://localhost:8080");
    }

    @Test
    public void testCalculateToMaxPopulated() {
        // Prepare test data
        City[] cities = new City[] {
            createCity(1L, "City1", 100000, 10.0, 20.0),
            createCity(2L, "City2", 200000, 30.0, 40.0),
            createCity(3L, "City3", 150000, 50.0, 60.0)
        };

        PageResponse<City> pageResponse = new PageResponse<>();
        pageResponse.setContent(Arrays.asList(cities));

        ResponseEntity<PageResponse<City>> responseEntity = new ResponseEntity<>(pageResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // Execute
        double distance = routeService.calculateToMaxPopulated();

        // Verify - distance from (0,0) to max populated city (30, 40)
        double expected = Math.sqrt(Math.pow(30, 2) + Math.pow(40, 2));
        assertEquals(expected, distance, 0.001);
    }

    @Test
    public void testCalculateToMaxPopulatedEmptyArray() {
        PageResponse<City> pageResponse = new PageResponse<>();
        pageResponse.setContent(Collections.emptyList());

        ResponseEntity<PageResponse<City>> responseEntity = new ResponseEntity<>(pageResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        double distance = routeService.calculateToMaxPopulated();

        assertEquals(0.0, distance);
    }

    @Test
    public void testCalculateBetweenOldestAndNewest() {
        // Prepare test data
        City[] cities = new City[] {
            createCityWithDate(1L, "Old", LocalDate.of(2020, 1, 1), 10.0, 20.0),
            createCityWithDate(2L, "New", LocalDate.of(2023, 1, 1), 30.0, 40.0),
            createCityWithDate(3L, "Middle", LocalDate.of(2021, 1, 1), 50.0, 60.0)
        };

        PageResponse<City> pageResponse = new PageResponse<>();
        pageResponse.setContent(Arrays.asList(cities));

        ResponseEntity<PageResponse<City>> responseEntity = new ResponseEntity<>(pageResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // Execute
        double distance = routeService.calculateBetweenOldestAndNewest();

        // Verify - distance from oldest (10,20) to newest (30,40)
        double expected = Math.sqrt(Math.pow(30 - 10, 2) + Math.pow(40 - 20, 2));
        assertEquals(expected, distance, 0.001);
    }

    @Test
    public void testCalculateBetweenOldestAndNewestLessThanTwoCities() {
        City[] cities = new City[] {createCityWithDate(1L, "Only", LocalDate.now(), 10.0, 20.0)};

        PageResponse<City> pageResponse = new PageResponse<>();
        pageResponse.setContent(Arrays.asList(cities));

        ResponseEntity<PageResponse<City>> responseEntity = new ResponseEntity<>(pageResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        double distance = routeService.calculateBetweenOldestAndNewest();

        assertEquals(0.0, distance);
    }

    private City createCity(Long id, String name, int population, double x, double y) {
        City city = new City();
        city.setId(id);
        city.setName(name);
        city.setPopulation(population);

        Coordinates coords = new Coordinates();
        coords.setX(x);
        coords.setY(y);
        city.setCoordinates(coords);

        city.setCreationDate(LocalDate.now());
        return city;
    }

    private City createCityWithDate(Long id, String name, LocalDate date, double x, double y) {
        City city = createCity(id, name, 100000, x, y);
        city.setCreationDate(date);
        return city;
    }
}
