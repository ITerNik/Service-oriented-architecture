package ru.ifmo.calculatingservice.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.ifmo.calculatingservice.service.RouteService;

@WebMvcTest(RouteController.class)
public class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    @Test
    public void testCalculateToMaxPopulated() throws Exception {
        // Mock service response
        when(routeService.calculateToMaxPopulated()).thenReturn(50.0);

        // Perform request and verify
        mockMvc.perform(get("/route/calculate/to-max-populated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distance").value(50.0));
    }

    @Test
    public void testCalculateToMaxPopulatedError() throws Exception {
        // Mock service to throw exception
        when(routeService.calculateToMaxPopulated()).thenThrow(new RuntimeException("Service error"));

        // Perform request and verify error response
        mockMvc.perform(get("/route/calculate/to-max-populated")).andExpect(status().isInternalServerError());
    }

    @Test
    public void testCalculateBetweenOldestAndNewest() throws Exception {
        // Mock service response
        when(routeService.calculateBetweenOldestAndNewest()).thenReturn(100.5);

        // Perform request and verify
        mockMvc.perform(get("/route/calculate/between-oldest-and-newest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distance").value(100.5));
    }

    @Test
    public void testCalculateBetweenOldestAndNewestError() throws Exception {
        // Mock service to throw exception
        when(routeService.calculateBetweenOldestAndNewest()).thenThrow(new RuntimeException("Service error"));

        // Perform request and verify error response
        mockMvc.perform(get("/route/calculate/between-oldest-and-newest")).andExpect(status().isInternalServerError());
    }
}
