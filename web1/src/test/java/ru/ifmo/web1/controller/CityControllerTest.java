package ru.ifmo.web1.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.ifmo.web1.model.City;
import ru.ifmo.web1.model.Climate;
import ru.ifmo.web1.model.Coordinates;
import ru.ifmo.web1.model.Human;
import ru.ifmo.web1.repository.CityRepository;
import ru.ifmo.web1.service.CityService;

@SpringBootTest
class CityControllerTest {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CityService cityService;

    private City testCity;

    @BeforeEach
    void setUp() {
        cityRepository.deleteAll();
        testCity = createTestCity();
    }

    @Test
    void testCreateCity() {
        City created = cityService.createCity(testCity);
        assertNotNull(created.getId());
        assertEquals("TestCity", created.getName());
        assertTrue(cityRepository.existsById(created.getId()));
    }

    @Test
    void testGetCityById() {
        City saved = cityRepository.save(testCity);
        City retrieved = cityService.getCityById(saved.getId());
        assertNotNull(retrieved);
        assertEquals("TestCity", retrieved.getName());
    }

    @Test
    void testDeleteCity() {
        City saved = cityRepository.save(testCity);
        Long id = saved.getId();
        assertTrue(cityService.deleteCity(id));
        assertFalse(cityRepository.existsById(id));
    }

    @Test
    void testUpdateCity() {
        City saved = cityRepository.save(testCity);
        saved.setName("UpdatedCity");
        City updated = cityService.updateCity(saved.getId(), saved);
        assertEquals("UpdatedCity", updated.getName());
    }

    @Test
    void testGetCityWithMinName() {
        cityRepository.save(testCity);
        City city2 = createTestCity();
        city2.setName("AnotherCity");
        cityRepository.save(city2);

        City minName = cityService.getCityWithMinName();
        assertNotNull(minName);
    }

    @Test
    void testDeleteCityByMeters() {
        testCity.setMetersAboveSeaLevel(100);
        cityRepository.save(testCity);
        boolean deleted = cityService.deleteCityByMeters(200);
        assertTrue(deleted);
    }

    private City createTestCity() {
        City city = new City();
        city.setName("TestCity");

        Coordinates coords = new Coordinates();
        coords.setX(10.0);
        coords.setY(-50.0);
        city.setCoordinates(coords);

        city.setArea(1000);
        city.setPopulation(100000);
        city.setClimate(Climate.HUMIDCONTINENTAL);
        city.setCreationDate(LocalDate.now());

        Human governor = new Human();
        governor.setHeight(1.8);
        governor.setBirthday(LocalDate.of(1980, 1, 1));
        city.setGovernor(governor);

        return city;
    }
}
