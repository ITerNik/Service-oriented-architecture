package ru.ifmo.web1.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ifmo.web1.model.City;
import ru.ifmo.web1.model.Climate;
import ru.ifmo.web1.model.Coordinates;
import ru.ifmo.web1.model.Human;
import ru.ifmo.web1.repository.CityRepository;

@SpringBootTest
@AutoConfigureMockMvc
class CityControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CityRepository cityRepository;

    @BeforeEach
    void setUp() {
        cityRepository.deleteAll();
    }

    @Test
    void testGetCities() throws Exception {
        mockMvc.perform(get("/cities")).andExpect(status().isOk());
    }

    @Test
    void testCreateCity() throws Exception {
        String jsonContent =
                """
            {
              "name": "TestCity",
              "coordinates": {
                "x": 10.5,
                "y": -50.0
              },
              "area": 1000,
              "population": 100000,
              "climate": "HUMIDCONTINENTAL",
              "governor": {
                "height": 1.8
              }
            }
            """;

        mockMvc.perform(post("/cities").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("TestCity"));
    }

    @Test
    void testGetCityById() throws Exception {
        City city = new City();
        city.setName("TestCity");
        city.setCoordinates(new Coordinates());
        city.getCoordinates().setX(10.0);
        city.getCoordinates().setY(-50.0);
        city.setArea(1000);
        city.setPopulation(100000);
        city.setClimate(Climate.HUMIDCONTINENTAL);
        city.setGovernor(new Human());
        city.getGovernor().setHeight(1.8);
        City saved = cityRepository.save(city);

        mockMvc.perform(get("/cities/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestCity"));
    }

    @Test
    void testDeleteCity() throws Exception {
        City city = new City();
        city.setName("TestCity");
        city.setCoordinates(new Coordinates());
        city.getCoordinates().setX(10.0);
        city.getCoordinates().setY(-50.0);
        city.setArea(1000);
        city.setPopulation(100000);
        city.setClimate(Climate.HUMIDCONTINENTAL);
        city.setGovernor(new Human());
        city.getGovernor().setHeight(1.8);
        City saved = cityRepository.save(city);

        mockMvc.perform(delete("/cities/" + saved.getId())).andExpect(status().isNoContent());
    }
}
